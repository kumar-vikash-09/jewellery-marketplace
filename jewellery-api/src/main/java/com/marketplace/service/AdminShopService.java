package com.marketplace.service;

import com.marketplace.entity.Shop;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminShopService {

    private final ShopRepository shopRepository;

    public AdminShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Transactional
    public String verifyShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found with id: " + shopId));
        shop.setVerified(true);
        shopRepository.save(shop);
        return "Shop verified successfully";
    }
}
