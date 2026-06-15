package com.mateo.wallet.transaction.dto;

import com.mateo.wallet.transaction.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        TransactionType type,
        String direction,
        String counterpartName,
        LocalDateTime createdAt
) {}