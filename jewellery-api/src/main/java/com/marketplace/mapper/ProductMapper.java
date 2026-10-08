package com.marketplace.mapper;

import com.marketplace.dto.response.ProductResponse;
import com.marketplace.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponse(List<Product> products);
}
