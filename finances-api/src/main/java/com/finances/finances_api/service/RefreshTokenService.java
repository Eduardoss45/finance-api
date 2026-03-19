package com.finances.finances_api.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.finances.finances_api.domain.RefreshToken;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.exception.UnauthorizedException;
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
        String tokenValue = UUID.randomUUID().toString();
        RefreshToken token = refreshTokenRepository.findByUser(user).orElseGet(RefreshToken::new);

        token.setUser(user);
        token.setToken(tokenValue);
        token.setExpiresAt(Instant.now().plusMillis(refreshExpiration));
        refreshTokenRepository.save(token);
        return tokenValue;
    }

    public RefreshToken validate(String token) {
        RefreshToken entity = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (entity.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired");
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
