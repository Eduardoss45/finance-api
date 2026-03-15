package com.finances.finances_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finances.finances_api.dto.auth.LoginRequest;
import com.finances.finances_api.dto.auth.LoginResponse;
import com.finances.finances_api.dto.auth.RefreshRequest;
import com.finances.finances_api.dto.auth.RegisterRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/register")
    public ResponseEntity<RegisterRequest> register(@RequestBody RegisterRequest dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest dto) {
        LoginResponse response = new LoginResponse();
        response.setAccessToken("jwt_token");
        response.setRefreshToken("refresh_token");
        response.setExpiresIn(3600);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshRequest> refresh(@RequestBody RefreshRequest dto) {
        return ResponseEntity.ok(dto);
    }
}
