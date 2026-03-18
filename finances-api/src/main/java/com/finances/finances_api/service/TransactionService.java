package com.finances.finances_api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.finances.finances_api.domain.Account;
import com.finances.finances_api.domain.Transaction;
import com.finances.finances_api.domain.enums.TransactionType;
import com.finances.finances_api.dto.transaction.TransactionRequest;
import com.finances.finances_api.dto.transaction.TransactionResponse;
import com.finances.finances_api.mapper.TransactionMapper;
import com.finances.finances_api.repository.AccountRepository;
import com.finances.finances_api.repository.TransactionRepository;
import com.finances.finances_api.security.UserMain;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse create(UUID accountId, TransactionRequest request, UserMain requester) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(requester.getUser().getId())) {
            throw new RuntimeException("Forbidden");
        }

        BigDecimal balance = transactionRepository.findByAccountId(accountId).stream()
                .map(tx -> tx.getType() == TransactionType.CREDIT ? tx.getAmount() : tx.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (request.getType() == TransactionType.DEBIT) {
            if (balance.compareTo(request.getAmount()) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
        }

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(request.getType());
        tx.setAmount(request.getAmount());
        transactionRepository.save(tx);

        return TransactionMapper.toResponse(tx);
    }

    public List<TransactionResponse> listByAccount(UUID accountId, UserMain requester) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (!account.getUser().getId().equals(requester.getUser().getId())) {
            throw new RuntimeException("Forbidden");
        }

        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream().map(TransactionMapper::toResponse).toList();
    }
}
