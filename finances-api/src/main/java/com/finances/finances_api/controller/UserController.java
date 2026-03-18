package com.finances.finances_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finances.finances_api.dto.user.UserResponse;
import com.finances.finances_api.dto.user.UserSummaryResponse;
import com.finances.finances_api.security.UserMain;
import com.finances.finances_api.service.UserService;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserSummaryResponse> listAll() {
        return userService.listAll();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id, @AuthenticationPrincipal UserMain requester) {
        return userService.getById(id, requester);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id, @AuthenticationPrincipal UserMain requester) {
        userService.deactivate(id, requester);
        return ResponseEntity.noContent().build();
    }
}
