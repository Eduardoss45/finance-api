package com.finances.finances_api.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.finances.finances_api.domain.RefreshToken;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createFor(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setToken(token);
        entity.setExpiresAt(Instant.now().plusMillis(refreshExpiration));
        refreshTokenRepository.save(entity);
        return token;
    }

    public RefreshToken validate(String token) {
        RefreshToken entity = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (entity.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return entity;
    }

    public String rotate(RefreshToken token) {
        String newToken = UUID.randomUUID().toString();
        token.setToken(newToken);
        token.setExpiresAt(Instant.now().plusMillis(refreshExpiration));
        refreshTokenRepository.save(token);
        return newToken;
    }
}
