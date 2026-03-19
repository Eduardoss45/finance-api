package com.finances.finances_api.service;

import com.finances.finances_api.repository.AccountRepository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.finances.finances_api.audit.Audited;
import com.finances.finances_api.domain.Account;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.dto.account.AccountRequest;
import com.finances.finances_api.dto.account.AccountResponse;
import com.finances.finances_api.exception.ForbiddenException;
import com.finances.finances_api.exception.NotFoundException;
import com.finances.finances_api.mapper.AccountMapper;
import com.finances.finances_api.security.UserMain;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Audited(action = "ACCOUNT_CREATED", entity = "Account")
    public AccountResponse create(AccountRequest request, UserMain requester) {
        User user = requester.getUser();
        
        Account account = new Account();
        account.setUser(user);
        account.setName(request.getName());
        account.setActive(true);
        
        accountRepository.save(account);
        
        return AccountMapper.toResponse(account);
    }
    
    public Page<AccountResponse> listAll(Pageable pageable, UserMain requester) {
        return accountRepository.findByUserId(requester.getUser().getId(), pageable)
                .map(AccountMapper::toResponse);
    }
    
    public AccountResponse getById(UUID id, UserMain requester) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));

        if (!account.getUser().getId().equals(requester.getUser().getId())) {
            throw new ForbiddenException("Forbidden");
        }
        
        return AccountMapper.toResponse(account);
    }
    
    @Audited(action = "ACCOUNT_DELETED", entity = "Account")
    public void delete(UUID id, UserMain requester) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));

        if (!account.getUser().getId().equals(requester.getUser().getId())) {
            throw new ForbiddenException("Forbidden");
        }

        accountRepository.delete(account);
    }
}
