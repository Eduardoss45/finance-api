package com.finances.finances_api.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.finances.finances_api.audit.Audited;
import com.finances.finances_api.domain.Account;
import com.finances.finances_api.domain.Transaction;
import com.finances.finances_api.domain.enums.TransactionType;
import com.finances.finances_api.dto.transaction.TransactionRequest;
import com.finances.finances_api.dto.transaction.TransactionResponse;
import com.finances.finances_api.exception.BadRequestException;
import com.finances.finances_api.exception.ForbiddenException;
import com.finances.finances_api.exception.NotFoundException;
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
    @Audited(action = "TRANSACTION_CREATED", entity = "Transaction")
    public TransactionResponse create(UUID accountId, TransactionRequest request, UserMain requester) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (!account.getUser().getId().equals(requester.getUser().getId())) {
            throw new ForbiddenException("Forbidden");
        }

        BigDecimal balance = account.getBalance();

        if (request.getType() == TransactionType.DEBIT) {
            if (balance.compareTo(request.getAmount()) < 0) {
                throw new BadRequestException("Insufficient balance");
            }
        }

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(request.getType());
        tx.setAmount(request.getAmount());
        transactionRepository.save(tx);

        if (request.getType() == TransactionType.CREDIT) {
            account.setBalance(balance.add(request.getAmount()));
        } else {
            account.setBalance(balance.subtract(request.getAmount()));
        }
        accountRepository.save(account);

        return TransactionMapper.toResponse(tx);
    }

    public Page<TransactionResponse> listByAccount(UUID accountId, Pageable pageable, UserMain requester) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        if (!account.getUser().getId().equals(requester.getUser().getId())) {
            throw new ForbiddenException("Forbidden");
        }

        return transactionRepository.findByAccountId(accountId, pageable)
                .map(TransactionMapper::toResponse);
    }
}
