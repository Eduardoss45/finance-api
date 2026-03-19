package com.finances.finances_api.dto.transaction;

import java.math.BigDecimal;

import com.finances.finances_api.domain.enums.TransactionType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @NotNull
    private TransactionType type;

    @NotNull
    @Positive
    @Digits(integer = 15, fraction = 2)
    private BigDecimal amount;
}
