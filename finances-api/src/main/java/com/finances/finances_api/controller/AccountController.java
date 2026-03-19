package com.finances.finances_api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finances.finances_api.dto.account.AccountRequest;
import com.finances.finances_api.dto.account.AccountResponse;
import com.finances.finances_api.security.UserMain;
import com.finances.finances_api.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request,
            @AuthenticationPrincipal UserMain requester) {
        AccountResponse response = accountService.create(request, requester);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public Page<AccountResponse> listAll(Pageable pageable, @AuthenticationPrincipal UserMain requester) {
        return accountService.listAll(pageable, requester);
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable UUID id, @AuthenticationPrincipal UserMain requester) {
        return accountService.getById(id, requester);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal UserMain requester) {
        accountService.delete(id, requester);
        return ResponseEntity.noContent().build();
    }
}
