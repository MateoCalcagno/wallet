package com.mateo.wallet.transaction.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;

import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.model.TransactionType;

public interface TransactionService {
    void transfer(String fromEmail, String identifier, BigDecimal amount);
    Page<TransactionResponse> getHistory(String email, int page, int size, TransactionType type);
}