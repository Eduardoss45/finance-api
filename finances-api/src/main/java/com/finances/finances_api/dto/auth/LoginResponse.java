package com.finances.finances_api.dto.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
}
