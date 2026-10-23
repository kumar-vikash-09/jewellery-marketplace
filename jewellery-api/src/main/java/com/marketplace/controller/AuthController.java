package com.marketplace.controller;

import com.marketplace.dto.request.LoginRequest;
import com.marketplace.dto.request.SellerRegistrationRequest;
import com.marketplace.dto.response.AuthResponse;
import com.marketplace.service.AuthService;
import com.marketplace.service.SellerRegistrationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SellerRegistrationService sellerRegistrationService;

    public AuthController(AuthService authService, SellerRegistrationService sellerRegistrationService) {
        this.authService = authService;
        this.sellerRegistrationService = sellerRegistrationService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register-seller")
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public Map<String, String> registerSeller(@Valid @RequestBody SellerRegistrationRequest request) {
        String message =
                sellerRegistrationService.registerSeller(request);
        return Map.of("message", message);
    }
}
