package com.finances.finances_api.mapper;

import com.finances.finances_api.domain.Transaction;
import com.finances.finances_api.dto.transaction.TransactionResponse;

public class TransactionMapper {
    public static TransactionResponse toResponse(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getAccount().getId(),
                tx.getType(),
                tx.getAmount(),
                tx.getCreatedAt());
    }
}