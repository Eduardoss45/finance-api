package com.finances.finances_api.domain.enums;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TransactionType {
    CREDIT,
    DEBIT;

    @JsonCreator
    public static TransactionType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Transaction type cannot be null or blank");
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);

        return switch (normalized) {
            case "CREDIT", "DEPOSIT" -> CREDIT;
            case "DEBIT", "WITHDRAW", "WITHDRAWAL" -> DEBIT;
            default -> throw new IllegalArgumentException("Invalid transaction type: " + value);
        };
    }
}
