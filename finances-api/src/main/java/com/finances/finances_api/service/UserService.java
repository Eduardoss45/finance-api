package com.finances.finances_api.service;

import com.finances.finances_api.repository.UserRepository;
import com.finances.finances_api.security.UserMain;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.finances.finances_api.domain.User;
import com.finances.finances_api.dto.user.UserResponse;
import com.finances.finances_api.dto.user.UserSummaryResponse;
import com.finances.finances_api.mapper.UserMapper;

@Service
public class UserService {
    private final UserRepository userRepository;

    private boolean isAdmin(UserMain requester) {
        return requester.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSummaryResponse> listAll() {
        return userRepository.findAll().stream().map(UserMapper::toSummary).toList();
    }

    public UserResponse getById(UUID id, UserMain requester) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (!isAdmin(requester) && !user.getId().equals(requester.getUser().getId())) {
            throw new RuntimeException("Forbidden");
        }

        return UserMapper.toResponse(user);
    }

    public void deactivate(UUID id, UserMain requester) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (!isAdmin(requester) && !user.getId().equals(requester.getUser().getId())) {
            throw new RuntimeException("Forbidden");
        }

        user.setActive(false);
        userRepository.save(user);
    }
}
