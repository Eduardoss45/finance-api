package com.finances.finances_api.dto.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.finances.finances_api.domain.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private UUID id;
    private UUID accountId;
    private TransactionType type;
    private BigDecimal amount;
    private Instant createdAt;
}
