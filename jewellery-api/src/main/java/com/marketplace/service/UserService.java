package com.marketplace.service;


import com.marketplace.config.PasswordConfig;
import com.marketplace.dto.request.UserCreateRequest;
import com.marketplace.dto.response.UserResponse;
import com.marketplace.entity.User;
import com.marketplace.enums.UserRole;
import com.marketplace.exception.DuplicateResourceException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.mapper.UserMapper;
import com.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordConfig passwordencoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordConfig passwordencoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordencoder = passwordencoder;
        this.userMapper = userMapper;
    }

    public UserResponse createuser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        User user = User.builder().name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password_hash(passwordencoder.passwordEncoder(request.getPassword()).toString())
                .role(UserRole.CUSTOMER)
                .build();
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponse(users);
    }


}
