package com.finances.finances_api.domain.enums;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    @JsonCreator
    public static Role fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or blank");
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);

        return switch (normalized) {
            case "ROLE_USER", "USER" -> ROLE_USER;
            case "ROLE_ADMIN", "ADMIN" -> ROLE_ADMIN;
            default -> throw new IllegalArgumentException("Invalid role: " + value);
        };
    }
}
