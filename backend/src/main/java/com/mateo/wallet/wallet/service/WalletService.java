package com.mateo.wallet.wallet.service;

import java.math.BigDecimal;

public interface WalletService {
    BigDecimal getBalance(String email);
    void deposit(String email, BigDecimal amount);
    void withdraw(String email, BigDecimal amount);
}