package com.marketplace.service;

import com.marketplace.dto.request.ShopCreateRequest;
import com.marketplace.dto.response.ShopResponse;
import com.marketplace.entity.Shop;
import com.marketplace.entity.User;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.mapper.ShopMapper;
import com.marketplace.repository.ShopRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {


    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final ShopMapper shopMapper;

    public ShopService(ShopRepository shopRepository, UserRepository userRepository, ShopMapper shopMapper) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
        this.shopMapper = shopMapper;
    }

    public ShopResponse createShop(ShopCreateRequest request) {

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Owner not found"));
        Shop shop = Shop.builder()
                .owner(owner)
                .name(request.getName())
                .description(request.getDescription())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .phone(request.getPhone())
                .verified(false)
                .build();
        Shop savedShop = shopRepository.save(shop);
        return shopMapper.toResponse(savedShop);
    }

    public ShopResponse getShopById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));
        return shopMapper.toResponse(shop);
    }

    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll().stream()
                .map(shopMapper::toResponse)
                .toList();
    }

    public List<ShopResponse> getShopsByCity(String city) {
        return shopRepository.findByCityIgnoreCase(city).stream()
                .map(shopMapper::toResponse)
                .toList();
    }
}
