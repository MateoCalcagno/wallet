package com.mateo.wallet.wallet.service;

import java.math.BigDecimal;

public interface WalletService {

    BigDecimal getBalance(Long userId);

    void deposit(Long userId, BigDecimal amount);

    void withdraw(Long userId, BigDecimal amount);
}