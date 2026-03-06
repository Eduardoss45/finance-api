package com.finances.finances_api;

import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.finances.finances_api.domain.Account;
import com.finances.finances_api.domain.Transaction;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.domain.enums.Role;
import com.finances.finances_api.domain.enums.TransactionType;
import com.finances.finances_api.repository.AccountRepository;
import com.finances.finances_api.repository.TransactionRepository;
import com.finances.finances_api.repository.UserRepository;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CommandLine {

    @Bean
    public CommandLineRunner testORM(
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        return args -> {

            System.out.println("===== ORM TEST START =====");

            User user = new User();
            user.setName("Eduardo");
            user.setEmail("edu@test.com");
            user.setPassword("123456");
            user.setRole(Role.fromValue("ADMIN"));
            user.setActive(true);
            user.setCreatedAt(Instant.now());

            userRepository.save(user);

            UUID userId = user.getId();

            System.out.println("User saved: " + userId);

            System.out.println("User exists by email: "
                    + userRepository.existsByEmail("edu@test.com"));

            userRepository.findByEmail("edu@test.com")
                    .ifPresent(u -> System.out.println("User found: " + u.getName()));

            Account account = new Account();
            account.setUser(user);
            account.setName("Main Account");
            account.setActive(true);
            account.setCreatedAt(Instant.now());

            accountRepository.save(account);

            UUID accountId = account.getId();

            System.out.println("Account saved: " + accountId);

            System.out.println("Accounts by user:");

            accountRepository.findByUserId(userId)
                    .forEach(a -> System.out.println("Account: " + a.getName()));

            Transaction deposit = new Transaction();
            deposit.setAccount(account);
            deposit.setType(TransactionType.fromValue("DEPOSIT"));
            deposit.setAmount(new BigDecimal("100.00"));
            deposit.setCreatedAt(Instant.now());

            transactionRepository.save(deposit);

            Transaction withdraw = new Transaction();
            withdraw.setAccount(account);
            withdraw.setType(TransactionType.fromValue("WITHDRAW"));
            withdraw.setAmount(new BigDecimal("40.00"));
            withdraw.setCreatedAt(Instant.now());

            transactionRepository.save(withdraw);

            System.out.println("Transactions saved");

            System.out.println("Transactions by account:");

            transactionRepository.findByAccountId(accountId)
                    .forEach(t -> System.out.println(
                            t.getType() + " -> " + t.getAmount()));

            System.out.println("Transactions ordered by date:");

            transactionRepository
                    .findByAccountIdOrderByCreatedAtDesc(accountId)
                    .forEach(t -> System.out.println(
                            t.getCreatedAt() + " | " + t.getAmount()));

            System.out.println("===== ORM TEST FINISHED =====");

        };
    }
}
