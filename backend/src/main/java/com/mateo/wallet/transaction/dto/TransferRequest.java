package com.mateo.wallet.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferRequest {

    @NotBlank(message = "CBU or alias is required")
    private String identifier;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    public TransferRequest() {}

    public String getIdentifier() { return identifier; }
    public BigDecimal getAmount() { return amount; }
}