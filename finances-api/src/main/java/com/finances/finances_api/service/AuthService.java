package com.finances.finances_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finances.finances_api.audit.Audited;
import com.finances.finances_api.domain.RefreshToken;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.domain.enums.Role;
import com.finances.finances_api.dto.auth.AuthResponse;
import com.finances.finances_api.dto.auth.LoginRequest;
import com.finances.finances_api.dto.auth.RefreshRequest;
import com.finances.finances_api.dto.auth.RegisterRequest;
import com.finances.finances_api.exception.ConflictException;
import com.finances.finances_api.exception.ForbiddenException;
import com.finances.finances_api.exception.UnauthorizedException;
import com.finances.finances_api.repository.UserRepository;
import com.finances.finances_api.security.JwtService;
import com.finances.finances_api.security.UserMain;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
            RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Audited(action = "USER_REGISTER", entity = "User")
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already registered");
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

        return new AuthResponse(user.getId(), accessToken, refreshToken, 3600);

    }

    @Audited(action = "USER_LOGIN", entity = "User")
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isActive()) {
            throw new ForbiddenException("User inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(new UserMain(user));
        String refreshToken = refreshTokenService.createFor(user);

        return new AuthResponse(user.getId(), accessToken, refreshToken, 3600);

    }

    @Audited(action = "TOKEN_REFRESH", entity = "RefreshToken")
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken token = refreshTokenService.validate(request.getRefreshToken());

        User user = token.getUser();
        String accessToken = jwtService.generateToken(new UserMain(user));
        String newRefreshToken = refreshTokenService.rotate(token);

        return new AuthResponse(user.getId(), accessToken, newRefreshToken, 3600);

    }
}
