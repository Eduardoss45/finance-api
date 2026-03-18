package com.finances.finances_api.mapper;

import com.finances.finances_api.domain.User;
import com.finances.finances_api.dto.user.UserResponse;
import com.finances.finances_api.dto.user.UserSummaryResponse;

public class UserMapper {
    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt());
    }

    public static UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive());
    }
}
