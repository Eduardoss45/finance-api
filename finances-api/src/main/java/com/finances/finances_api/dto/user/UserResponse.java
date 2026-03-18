package com.finances.finances_api.dto.user;

import java.time.Instant;
import java.util.UUID;

import com.finances.finances_api.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private boolean active;
    private Instant createdAt;
}
