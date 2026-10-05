package com.marketplace.service;

import com.marketplace.entity.Shop;
import com.marketplace.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {


    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Shop createShop(Shop shop) {
        return shopRepository.save(shop);
    }

    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public List<Shop> getShopsByCity(String city) {
        return shopRepository.findByCityIgnoreCase(city);
    }
}
