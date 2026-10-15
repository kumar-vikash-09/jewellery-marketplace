package com.marketplace.controller;

import com.marketplace.dto.request.LoginRequest;
import com.marketplace.dto.response.AuthResponse;
import com.marketplace.service.AuthService;
import com.marketplace.service.SellerRegistrationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
