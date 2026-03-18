package com.finances.finances_api.service;

import com.finances.finances_api.repository.AccountRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.finances.finances_api.domain.Account;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.dto.account.AccountRequest;
import com.finances.finances_api.dto.account.AccountResponse;
import com.finances.finances_api.mapper.AccountMapper;
import com.finances.finances_api.security.UserMain;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse create(AccountRequest request, UserMain requester) {
        User user = requester.getUser();

        Account account = new Account();
        account.setUser(user);
        account.setName(request.getName());
        account.setActive(true);

        accountRepository.save(account);

        return AccountMapper.toResponse(account);
    }

        public List<AccountResponse> listAll(UserMain requester) {
            List<Account> accounts = accountRepository.findByUserId(requester.getUser().getId());

            return accounts.stream().map(AccountMapper::toResponse).toList();
        }

    public AccountResponse getById(UUID id, UserMain requester) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(requester.getUser().getId())) {
            throw new RuntimeException("Forbidden");
        }

        return AccountMapper.toResponse(account);
    }

    public void delete(UUID id, UserMain requester) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(requester.getUser().getId())) {
            throw new RuntimeException("Forbidden");
        }

        accountRepository.delete(account);
    }
}
