# Security and Implementation Decision Log

This file records security-relevant decisions for the backend. Add new entries when an implementation choice changes authentication, authorization, data handling, or configuration. Keep the rationale and status current; do not record credentials, signing keys, or other secrets here.

## Authentication and API access

### DEC-001 — Store passwords as BCrypt hashes

- **Status:** Implemented
- **Where:** `jewellery-api/src/main/java/com/marketplace/config/PasswordConfig.java`, `jewellery-api/src/main/java/com/marketplace/service/UserService.java`
- **Decision:** Use Spring Security's `BCryptPasswordEncoder` to hash a password when creating a user. Authenticate using Spring Security's `AuthenticationManager`, which compares the submitted password with the stored hash.
- **Why:** Passwords should not be stored in plaintext or encrypted reversibly. BCrypt is a password-hashing function designed to make offline guessing more expensive. Reuse the same configured `PasswordEncoder` for registration and authentication rather than implementing password comparison manually.
- **Important:** Never return the password hash from an API response or log raw passwords.

### DEC-002 — Use stateless bearer tokens for API authentication

- **Status:** Implemented
- **Where:** `jewellery-api/src/main/java/com/marketplace/security/SecurityConfig.java`, `jewellery-api/src/main/java/com/marketplace/security/JwtService.java`, `jewellery-api/src/main/java/com/marketplace/security/JwtAuthenticationFilter.java`
- **Decision:** After successful login, issue a signed JWT. Clients send it on later requests in the HTTP `Authorization` header using the Bearer scheme. Do not create server-side HTTP sessions.
- **Why:** Stateless authentication suits the API request model and avoids server-side session state. The server verifies the signature and expiration before establishing an authenticated Spring Security context.
- **Trade-off:** A token remains usable until it expires unless revocation or a server-side token strategy is added. Keep the expiration bounded and protect the signing key.

### DEC-003 — Require authentication by default; explicitly allow public routes

- **Status:** Implemented
- **Where:** `jewellery-api/src/main/java/com/marketplace/security/SecurityConfig.java`
- **Decision:** Permit `/api/auth/**`, `/api/health`, and the configured Swagger/OpenAPI routes. Require authentication for all other requests.
- **Why:** A default-deny policy reduces the chance that a newly added endpoint is accidentally exposed. Public routes are an explicit allowlist.
- **Trade-off:** `permitAll` allows access without a token; it does not provide authorization for an authenticated user. Review the allowlist when adding endpoints, and add role/ownership checks where required.

### DEC-004 — Disable CSRF only for the stateless bearer-token API

- **Status:** Implemented; review if the authentication model changes
- **Where:** `jewellery-api/src/main/java/com/marketplace/security/SecurityConfig.java`
- **Decision:** Disable CSRF protection because the current API authenticates using explicitly supplied bearer tokens and does not use an automatically attached browser session cookie.
- **Why:** CSRF primarily protects cookie-authenticated browser requests. It is generally not applicable to a token that the client explicitly places in the authorization header.
- **Trade-off:** If browser authentication later moves to cookies, re-evaluate CSRF protection before shipping that change. This decision does not replace CORS configuration or XSS defenses.

### DEC-005 — Load current user details when processing a JWT

- **Status:** Implemented
- **Where:** `jewellery-api/src/main/java/com/marketplace/security/CustomUserDetailsService.java`, `jewellery-api/src/main/java/com/marketplace/security/JwtAuthenticationFilter.java`
- **Decision:** Use the JWT subject (email) to load the user from the repository and populate Spring Security with the user's current authorities.
- **Why:** Authorization uses the persisted role rather than trusting a client-provided identity or role. A missing user cannot become authenticated merely by presenting a token with an email subject.
- **Trade-off:** Each authenticated request incurs a user lookup. If this is optimized later, preserve signature/expiry validation and think through how role changes or account disablement take effect.

## Configuration and outstanding hardening

### DEC-006 — Keep database credentials and JWT signing keys out of tracked configuration

- **Status:** Follow-up required
- **Where:** `jewellery-api/src/main/resources/application.yaml`
- **Decision:** Do not put real database passwords or JWT signing keys in source-controlled configuration. Bind configuration to environment variables or a managed secret store, and provide safe local development values outside tracked files.
- **Why:** Anyone who can read repository history may be able to use a committed credential or signing key. A leaked JWT key can allow forged tokens; a leaked database password can expose or modify data.
- **Current state:** `application.yaml` currently contains literal values for the database password and `jwt.secret`. They are intentionally not copied into this log. If either value is real or has been shared/committed, rotate it and remove it from active configuration and repository history as appropriate.
- **Follow-up:** Use Spring placeholders such as `${DB_PASSWORD}` and `${JWT_SECRET}` (with no production secret fallback), supply the values through the runtime environment/secret manager, and verify startup fails clearly when required values are absent. Ensure local secrets files are ignored by Git.

### DEC-007 — Do not silently ignore token-processing failures

- **Status:** Follow-up required
- **Where:** `jewellery-api/src/main/java/com/marketplace/security/JwtAuthenticationFilter.java`
- **Decision:** Invalid, expired, malformed, or otherwise unprocessable bearer tokens must not authenticate a request. Replace the current empty exception handler with deliberate error handling: leave the request unauthenticated so protected routes are denied, and record a safe diagnostic without logging the token or secrets. Decide whether malformed tokens on public routes should be ignored or rejected, then apply that behavior consistently.
- **Why:** The current empty `catch` hides operational and implementation errors and makes security behavior difficult to diagnose. Never log raw bearer tokens; they are credentials.
- **Current behavior:** The filter catches all exceptions and continues the chain. Protected routes should still be denied because the security context remains unauthenticated, but the failure is silent. This is not a completed hardening fix.

### DEC-008 — Return the persisted role in the login response

- **Status:** Implemented
- **Where:** `jewellery-api/src/main/java/com/marketplace/service/AuthService.java`
- **Decision:** Read the authenticated user's role from the persisted user record and return that role in `AuthResponse`.
- **Why:** A hard-coded role makes the response inaccurate for any other user role and can cause clients to make incorrect authorization/UI decisions. Authorization must remain enforced by the backend regardless of the role reported to the client.
- **Current behavior:** After `AuthenticationManager.authenticate(...)` succeeds, `AuthService` queries `UserRepository` for the user and uses `getRole().name()` in the response. Do not treat the response role as an authorization control.

### DEC-009 — Use Java camelCase while preserving the database column name

- **Status:** Implemented
- **Where:** `jewellery-api/src/main/java/com/marketplace/entity/User.java`, `jewellery-api/src/main/java/com/marketplace/service/UserService.java`, `jewellery-api/src/main/java/com/marketplace/security/CustomUserDetailsService.java`
- **Decision:** Name the Java entity property `passwordHash` and explicitly map it to the existing `password_hash` database column with `@Column(name = "password_hash")`. Use Lombok's generated `passwordHash(...)` builder method and `getPasswordHash()` accessor consistently.
- **Why:** Java property names follow camelCase conventions and produce predictable Lombok accessors. The explicit JPA column mapping preserves compatibility with the existing Flyway schema, so this code-level cleanup does not require a database migration or rename stored data.

## Updating this log

For each new decision, record a short ID, status (`Proposed`, `Implemented`, `Follow-up required`, or `Superseded`), affected files, the chosen approach, and why alternatives were not selected. Update or supersede old entries when the code changes.
