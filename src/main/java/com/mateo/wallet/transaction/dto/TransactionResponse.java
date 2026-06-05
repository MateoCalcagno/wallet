package com.mateo.wallet.transaction.dto;

import com.mateo.wallet.transaction.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String direction; // "SENT" o "RECEIVED"
    private LocalDateTime createdAt;

    public TransactionResponse(Long id, BigDecimal amount, TransactionType type,
                                String direction, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.direction = direction;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public String getDirection() { return direction; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}