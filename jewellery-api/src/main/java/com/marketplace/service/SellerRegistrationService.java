package com.marketplace.service;

import com.marketplace.dto.request.SellerRegistrationRequest;
import com.marketplace.entity.Shop;
import com.marketplace.entity.User;
import com.marketplace.enums.UserRole;
import com.marketplace.exception.DuplicateResourceException;
import com.marketplace.repository.ShopRepository;
import com.marketplace.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SellerRegistrationService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final PasswordEncoder passwordEncoder;

    public SellerRegistrationService(UserRepository userRepository, ShopRepository shopRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String registerSeller(SellerRegistrationRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.SELLER)
                .build();

        User savedUser = userRepository.save(user);

        Shop shop = Shop.builder()
                .owner(savedUser)
                .name(request.getShopName())
                .description(request.getDescription())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .verified(false)
                .build();
        shopRepository.save(shop);
        return "Seller registered successfully. Shop verification is pending.";
    }
}
