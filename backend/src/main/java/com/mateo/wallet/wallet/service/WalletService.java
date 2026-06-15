package com.mateo.wallet.wallet.service;

import java.math.BigDecimal;

import com.mateo.wallet.user.model.User;
import com.mateo.wallet.wallet.dto.WalletResponse;
import com.mateo.wallet.wallet.model.Wallet;

public interface WalletService {
    WalletResponse getBalance(String email);
    void deposit(String email, BigDecimal amount);
    void withdraw(String email, BigDecimal amount);
    void updateAlias(String email, String alias);
    Wallet createForUser(User user);
    Wallet getByUserId(Long userId);
}