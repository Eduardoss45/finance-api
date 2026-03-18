package com.finances.finances_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finances.finances_api.domain.RefreshToken;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.domain.enums.Role;
import com.finances.finances_api.dto.auth.AuthResponse;
import com.finances.finances_api.dto.auth.LoginRequest;
import com.finances.finances_api.dto.auth.RefreshRequest;
import com.finances.finances_api.dto.auth.RegisterRequest;
import com.finances.finances_api.repository.UserRepository;
import com.finances.finances_api.security.JwtService;
import com.finances.finances_api.security.UserMain;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setActive(true);

        userRepository.save(user);

        String accessToken = jwtService.generateToken(new UserMain(user));
        String refreshToken = refreshTokenService.createFor(user);

        return new AuthResponse(accessToken, refreshToken, 3600);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(new UserMain(user));
        String refreshToken = refreshTokenService.createFor(user);

        return new AuthResponse(accessToken, refreshToken, 3600);
    }

    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken token = refreshTokenService.validate(request.getRefreshToken());

        User user = token.getUser();
        String accessToken = jwtService.generateToken(new UserMain(user));
        String newRefreshToken = refreshTokenService.rotate(token);

        return new AuthResponse(accessToken, newRefreshToken, 3600);
    }
}
