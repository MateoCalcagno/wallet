package com.mateo.wallet.transaction.service;

import java.math.BigDecimal;
import java.util.List;
import com.mateo.wallet.transaction.dto.TransactionResponse;

public interface TransactionService {
    void transfer(String fromEmail, String toEmail, BigDecimal amount);
    List<TransactionResponse> getHistory(String email);
}