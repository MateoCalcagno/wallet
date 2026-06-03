package com.mateo.wallet.wallet.service;

import com.mateo.wallet.common.exception.InsufficientBalanceException;
import com.mateo.wallet.common.exception.ResourceNotFoundException;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public BigDecimal getBalance(Long userId) {

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        return wallet.getBalance();
    }

    @Override
    @Transactional
    public void deposit(Long userId, BigDecimal amount) {

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        wallet.deposit(amount);
    }

    @Override
    @Transactional
    public void withdraw(Long userId, BigDecimal amount) {

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        wallet.withdraw(amount);
    }
}