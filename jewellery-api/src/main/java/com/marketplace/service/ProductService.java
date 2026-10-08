package com.marketplace.service;

import com.marketplace.dto.request.ProductCreateRequest;
import com.marketplace.dto.response.ProductResponse;
import com.marketplace.entity.Category;
import com.marketplace.entity.Product;
import com.marketplace.entity.Shop;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.mapper.ProductMapper;
import com.marketplace.repository.CategoryRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {


    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, ShopRepository shopRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.shopRepository = shopRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(ProductCreateRequest request) {

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Product newProduct = Product.builder()
                .shop(shop)
                .sku(request.getSku())
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .isActive(true)
                .build();
        Product savedProduct = productRepository.save(newProduct);
        return productMapper.toResponse(savedProduct);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productMapper.toResponse(productRepository.findAll());
    }

    public List<ProductResponse> getActiveProducts() {
        return productMapper.toResponse(productRepository.findByIsActiveTrue());
    }

    public List<ProductResponse> getProductsByShop(Long shopId) {
        return productMapper.toResponse(productRepository.findByShopId(shopId));
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productMapper.toResponse(productRepository.findByCategoryId(categoryId));
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productMapper.toResponse(productRepository.findByNameContainingIgnoreCase(keyword));
    }
}
