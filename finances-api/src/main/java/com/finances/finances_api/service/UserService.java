package com.finances.finances_api.service;

import com.finances.finances_api.repository.UserRepository;
import com.finances.finances_api.security.UserMain;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.finances.finances_api.audit.Audited;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.dto.user.UserResponse;
import com.finances.finances_api.dto.user.UserSummaryResponse;
import com.finances.finances_api.exception.ForbiddenException;
import com.finances.finances_api.exception.NotFoundException;
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

    public Page<UserSummaryResponse> listAll(Pageable pageable, UserMain requester) {
        if (!isAdmin(requester)) {
            throw new ForbiddenException("Forbidden");
        }
        return userRepository.findAll(pageable).map(UserMapper::toSummary);
    }

    public UserResponse getById(UUID id, UserMain requester) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        if (!isAdmin(requester) && !user.getId().equals(requester.getUser().getId())) {
            throw new ForbiddenException("Forbidden");
        }

        return UserMapper.toResponse(user);
    }

    @Audited(action = "USER_DEACTIVATED", entity = "User")
    public void deactivate(UUID id, UserMain requester) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        if (!isAdmin(requester) && !user.getId().equals(requester.getUser().getId())) {
            throw new ForbiddenException("Forbidden");
        }

        user.setActive(false);
        userRepository.save(user);
    }
}
