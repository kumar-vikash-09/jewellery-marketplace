package com.marketplace.config;

import com.marketplace.entity.User;
import com.marketplace.enums.UserRole;
import com.marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class AdminInitializer {

    CommandLineRunner initializeAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                      @Value("${app.bootstrap-admin.email:}") String adminEmail,
                                      @Value("${app.bootstrap-admin.password:}") String adminPassword) {
        return args -> {
            if (adminEmail.isEmpty() || adminPassword.isEmpty()) {
                return; // Skip initialization if email or password is not provided
            }
            if (userRepository.existsByEmail(adminEmail)) {
                // Skip initialization if admin user already exists
            }
            User admin = User.builder()
                    .name("Marketplace Admin")
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(UserRole.ADMIN)
                    .build();
            userRepository.save(admin);
        };
    }

}
