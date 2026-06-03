package com.mateo.wallet.transaction.service;

import java.math.BigDecimal;

public interface TransactionService {

    void transfer(Long fromUserId, Long toUserId, BigDecimal amount);
}