package com.marketplace.controller;

import com.marketplace.dto.request.ProductCreateRequest;
import com.marketplace.dto.response.ProductResponse;
import com.marketplace.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.createProduct(request);
        return response;
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/active")
    public List<ProductResponse> getActiveProducts() {
        return productService.getActiveProducts();
    }

    @GetMapping("/shop/{shopId}")
    public List<ProductResponse> getByShop(
            @PathVariable Long shopId) {

        return productService.getProductsByShop(shopId);
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponse> getByCategory(
            @PathVariable Long categoryId) {

        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping("/search")
    public List<ProductResponse> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }
}
