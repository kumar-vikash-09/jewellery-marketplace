package com.marketplace.service;


import com.marketplace.dto.request.LoginRequest;
import com.marketplace.dto.response.AuthResponse;
import com.marketplace.repository.UserRepository;
import com.marketplace.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        String email = request.getEmail();
        String token = jwtService.generateToken(email);
        String role = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")).getRole().name();

        return new AuthResponse(token, email, role);
    }
}
