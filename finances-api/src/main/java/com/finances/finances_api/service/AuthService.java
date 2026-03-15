package com.finances.finances_api.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.finances.finances_api.dto.auth.LoginRequest;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.dto.auth.AuthResponse;
import com.finances.finances_api.dto.auth.RegisterRequest;
import com.finances.finances_api.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return new AuthResponse("accessToken", "refreshToken", 3600);
    }

//     public AuthResponse login(LoginRequest request) {
// 
//     }
}
