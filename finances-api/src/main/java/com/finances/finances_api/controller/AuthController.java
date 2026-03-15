package com.finances.finances_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finances.finances_api.dto.auth.LoginRequest;
import com.finances.finances_api.dto.auth.AuthResponse;
import com.finances.finances_api.dto.auth.RefreshRequest;
import com.finances.finances_api.dto.auth.RegisterRequest;
import com.finances.finances_api.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest dto) {
        AuthResponse response = authService.register(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest dto) {
        AuthResponse response = new AuthResponse();
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
