package com.marketplace.controller;


import com.marketplace.dto.request.ShopCreateRequest;
import com.marketplace.dto.response.ShopResponse;
import com.marketplace.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShopResponse createShop(@Valid @RequestBody ShopCreateRequest request) {
        return shopService.createShop(request);
    }

    @GetMapping("/{id}")
    public ShopResponse getShop(@PathVariable Long id) {
        return shopService.getShopById(id);
    }

    @GetMapping
    public List<ShopResponse> getAllShops() {
        return shopService.getAllShops();
    }

    @GetMapping("/city/{city}")
    public List<ShopResponse> getShopsByCity(@PathVariable String city) {
        return shopService.getShopsByCity(city);
    }
}
