package com.mateo.wallet.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferRequest {

    @Email(message = "Invalid email")
    @NotBlank(message = "Destination email is required")
    private String toEmail;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    public TransferRequest() {}

    public String getToEmail() { return toEmail; }
    public BigDecimal getAmount() { return amount; }
}