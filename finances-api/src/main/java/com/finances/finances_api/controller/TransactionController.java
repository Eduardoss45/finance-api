package com.finances.finances_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finances.finances_api.dto.transaction.TransactionRequest;
import com.finances.finances_api.dto.transaction.TransactionResponse;
import com.finances.finances_api.security.UserMain;
import com.finances.finances_api.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts/{id}/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal UserMain requester) {
        TransactionResponse response = transactionService.create(id, request, requester);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<TransactionResponse> list(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserMain requester) {
        return transactionService.listByAccount(id, requester);
    }
}
