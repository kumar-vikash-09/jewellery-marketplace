package com.marketplace.mapper;

import com.marketplace.dto.response.ShopResponse;
import com.marketplace.entity.Shop;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShopMapper {

    ShopResponse toResponse(Shop shop);

    List<ShopResponse> toResponse(List<Shop> shops);
}
