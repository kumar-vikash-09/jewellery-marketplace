package com.marketplace.service;

import com.marketplace.dto.request.CategoryCreateRequest;
import com.marketplace.dto.response.CategoryResponse;
import com.marketplace.entity.Category;
import com.marketplace.exception.DuplicateResourceException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.mapper.CategoryMapper;
import com.marketplace.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public CategoryResponse createCategory(CategoryCreateRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Category already exists");
        }

        Category.CategoryBuilder builder = Category.builder()
                .name(request.getName())
                .description(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = categoryRepository
                    .findById(request.getParentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Parent category not found"));
            builder.parent(parent);
        }

        Category savedCategory = categoryRepository.save(builder.build());
        return categoryMapper.toResponse(savedCategory);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toResponse(categoryRepository.findAll());
    }
}
