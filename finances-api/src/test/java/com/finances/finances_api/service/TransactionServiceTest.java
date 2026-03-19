package com.finances.finances_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.finances.finances_api.domain.Account;
import com.finances.finances_api.domain.Transaction;
import com.finances.finances_api.domain.User;
import com.finances.finances_api.domain.enums.TransactionType;
import com.finances.finances_api.dto.transaction.TransactionRequest;
import com.finances.finances_api.exception.BadRequestException;
import com.finances.finances_api.exception.ForbiddenException;
import com.finances.finances_api.repository.AccountRepository;
import com.finances.finances_api.repository.TransactionRepository;
import com.finances.finances_api.security.UserMain;

class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account account;
    private UserMain requester;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@test.com");

        accountId = UUID.randomUUID();
        account = new Account();
        account.setId(accountId);
        account.setUser(user);
        account.setBalance(new BigDecimal("100.00"));
        account.setCreatedAt(Instant.now());

        requester = new UserMain(user);
    }

    @Test
    void createCreditUpdatesBalance() {
        when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));

        TransactionRequest request = new TransactionRequest(TransactionType.CREDIT, new BigDecimal("50.00"));
        transactionService.create(accountId, request, requester);

        assertThat(account.getBalance()).isEqualByComparingTo("150.00");
        verify(transactionRepository).save(org.mockito.ArgumentMatchers.any(Transaction.class));
        verify(accountRepository).save(account);
    }

    @Test
    void createDebitInsufficientBalanceThrows() {
        when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));

        TransactionRequest request = new TransactionRequest(TransactionType.DEBIT, new BigDecimal("150.00"));

        assertThatThrownBy(() -> transactionService.create(accountId, request, requester))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Insufficient balance");
    }

    @Test
    void createForbiddenWhenNotOwner() {
        User other = new User();
        other.setId(UUID.randomUUID());
        UserMain otherRequester = new UserMain(other);

        when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));

        TransactionRequest request = new TransactionRequest(TransactionType.CREDIT, new BigDecimal("10.00"));

        assertThatThrownBy(() -> transactionService.create(accountId, request, otherRequester))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Forbidden");
    }
}
