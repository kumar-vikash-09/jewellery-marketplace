package com.marketplace.controller;

import com.marketplace.service.AdminShopService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminShopService adminShopService;

    public AdminController(AdminShopService adminShopService) {
        this.adminShopService = adminShopService;
    }

    @PatchMapping("/shops/{shopId}/verify")
    public Map<String, String> verifyShop(
            @PathVariable Long shopId) {

        String message = adminShopService.verifyShop(shopId);

        return Map.of("message", message);
    }
}
