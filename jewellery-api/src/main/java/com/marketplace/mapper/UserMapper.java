package com.marketplace.mapper;

import com.marketplace.dto.response.UserResponse;
import com.marketplace.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    List<UserResponse> toResponse(List<User> users);
}
