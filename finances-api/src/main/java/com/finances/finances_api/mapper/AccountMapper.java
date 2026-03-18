package com.finances.finances_api.mapper;

import com.finances.finances_api.domain.Account;
import com.finances.finances_api.dto.account.AccountResponse;

public class AccountMapper {
    public static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.isActive(),
                account.getCreatedAt());
    }
}
