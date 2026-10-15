# Backend Execution Flow

This document follows the current authentication implementation in `jewellery-api`. Paths are relative to the backend project root. Update the sequences when controllers, filters, or security rules change.

## Application startup and dependency wiring

1. Spring Boot starts `JewelleryApiApplication` and scans the `com.marketplace` package.
2. Spring creates the application beans, including:
   - `SecurityConfig` (`@Configuration`)
   - `JwtAuthenticationFilter` (`@Component`)
   - `JwtService` (`@Service`)
   - `CustomUserDetailsService` (`@Service`)
   - `AuthService` (`@Service`)
   - `PasswordConfig`, which contributes the `PasswordEncoder` bean
3. `JwtService` reads `jwt.secret` and `jwt.expiration` from configuration and prepares the signing key.
4. `SecurityConfig.securityFilterChain(...)` builds the HTTP security chain, adds the JWT filter before Spring's username/password authentication filter, and defines public versus protected paths.
5. `SecurityConfig.authenticationManager(...)` exposes Spring's `AuthenticationManager`. Spring uses `CustomUserDetailsService` and the configured password encoder for username/password authentication.
6. The app connects to PostgreSQL using `application.yaml`; Flyway validates/runs migrations, and Hibernate validates the mapped schema.

## Login: `POST /api/auth/login`

Request body:

```json
{
  "email": "you@example.com",
  "password": "your-password"
}
```

Call sequence:

1. `SecurityFilterChain` allows `/api/auth/**` without an existing JWT so a client can log in.
2. `AuthController.login(@Valid @RequestBody LoginRequest)` receives the JSON body.
3. Spring deserializes the body into `LoginRequest`; Jakarta validation checks that the email is present and formatted as an email, and that the password is not blank. Invalid input is rejected before the service call.
4. `AuthController` calls `AuthService.login(request)`.
5. `AuthService` creates a `UsernamePasswordAuthenticationToken` from the submitted email and raw password, then calls `AuthenticationManager.authenticate(...)`.
6. Spring's authentication provider calls `CustomUserDetailsService.loadUserByUsername(email)`.
7. `CustomUserDetailsService` calls `UserRepository.findByEmail(email)`.
   - If no user exists, it throws `ResourceNotFoundException`.
   - If found, it returns Spring `UserDetails` with the email, persisted password hash, and `ROLE_<role>` authority. The entity property is `passwordHash`, explicitly mapped by JPA to the existing `password_hash` database column.
8. Spring's password authentication checks the submitted password against the stored BCrypt hash using the configured `PasswordEncoder`.
   - If credentials are invalid, authentication fails; no token is generated.
   - If credentials are valid, control returns to `AuthService`.
9. `AuthService` calls `JwtService.generateToken(email)`.
10. `JwtService` creates a signed JWT with the email as subject, issue time, and configured expiration.
11. `AuthService` calls `UserRepository.findByEmail(email)` again and reads the persisted role.
12. `AuthService` returns `AuthResponse(token, email, role)`, where `role` is the persisted role name.
13. Spring serializes the response as JSON.

## Authenticated request with a bearer token

For protected requests, set the `Authorization` header to the `Bearer` scheme, followed by a space and the JWT returned from login.

Call sequence:

1. The request enters Spring Security's `SecurityFilterChain`.
2. `JwtAuthenticationFilter.doFilterInternal(...)` reads the `Authorization` header.
3. If the header is missing or does not start with `Bearer` followed by a space, the filter continues the chain without setting an authenticated user.
4. If it has the bearer prefix, the filter removes the prefix and calls `JwtService.extractEmail(token)`.
5. `JwtService` parses the signed token using the configured key and returns the token subject. Signature/parse errors throw an exception.
6. If the subject exists and the `SecurityContext` does not already contain an authentication, the filter calls `CustomUserDetailsService.loadUserByUsername(email)` to reload the user and authorities from PostgreSQL.
7. The filter calls `JwtService.isTokenValid(token, email)`. This checks that the token subject matches the expected email and that the token has not expired. Parsing/signature errors return `false`.
8. When valid, the filter creates a `UsernamePasswordAuthenticationToken` containing the loaded user and authorities, attaches web request details, then sets it in `SecurityContextHolder`.
9. The filter continues the chain.
10. `SecurityConfig` permits explicitly public routes. For other routes, Spring requires authentication; with a valid security context the request can proceed, and without it Spring denies access.
11. A controller handles an authorized request and Spring serializes its response.

**Current known issue:** `JwtAuthenticationFilter` catches exceptions from token parsing/user loading with an empty `catch` block and continues without authentication. Thus a protected route should still be rejected, but the reason is hidden. See `decision.md` DEC-007. Do not log the raw token when improving diagnostics.

## Authorization policy currently configured

`SecurityConfig.securityFilterChain(...)` currently:

- Disables CSRF.
- Sets session creation policy to `STATELESS`.
- Permits `/api/health`, `/api/auth/**`, `/swagger-ui/**`, `/swagger-ui.html`, and `/v3/api-docs/**`.
- Requires authentication for every other request.

The current chain enforces authentication, but these rules do not yet express per-role or resource-ownership checks. Add those where a business operation requires them; a role string in a response is not authorization.

## Configuration map

`jewellery-api/src/main/resources/application.yaml` currently configures:

- Application name and HTTP port (`8080`).
- PostgreSQL URL and username/password.
- JPA `open-in-view: false` and `ddl-auto: validate`.
- Flyway migration enablement and migration location.
- JWT signing secret and token lifetime (`jwt.expiration`, currently 86,400,000 ms / 24 hours).

Do not copy secret values into documentation, examples, logs, or API responses. The current YAML contains literal database and JWT values; see `decision.md` DEC-006 for the required configuration hardening.

## Seller registration status

`SellerRegistrationService.registerSeller(request)` currently checks for an existing email, BCrypt-hashes the new password, creates a `User` with the `SELLER` role, then creates an unverified `Shop`, all within a transaction. `SellerRegistrationRequest` defines the input fields and validation constraints.

`AuthController` currently injects `SellerRegistrationService`, but it has no seller-registration mapping and never calls the service. Therefore this flow is not currently reachable through an HTTP endpoint. Add a controller mapping that accepts `@Valid @RequestBody SellerRegistrationRequest` and calls `registerSeller(...)` when the endpoint is intended to be available. The configured `/api/auth/**` allowlist would make that path public, so consider whether public seller registration is intended and add any required verification or abuse controls.

## Trying the flow locally

1. Start PostgreSQL and ensure the configured database exists and its schema is migrated.
2. Start the API from `jewellery-api` with `.\mvnw.cmd spring-boot:run`.
3. Call `POST http://localhost:8080/api/auth/login` with a registered account's email and password.
4. Copy the returned token and send it in the `Authorization` header using the `Bearer` scheme when calling a protected API endpoint.
5. Try the same protected endpoint without a token; it should be denied.

Customer user creation in `UserService.createuser(...)` hashes passwords using the shared BCrypt `PasswordEncoder`, assigning the entity's `passwordHash` property; JPA persists it in `password_hash`. Seller creation logic is described above but is not currently exposed by an HTTP endpoint.
