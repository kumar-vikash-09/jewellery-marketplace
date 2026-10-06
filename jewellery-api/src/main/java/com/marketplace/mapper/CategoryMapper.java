package com.marketplace.mapper;

import com.marketplace.dto.response.CategoryResponse;
import com.marketplace.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponse(List<Category> categories);
}
