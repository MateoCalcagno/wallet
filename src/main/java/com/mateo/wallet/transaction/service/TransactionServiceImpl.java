package com.mateo.wallet.transaction.service;

import com.mateo.wallet.transaction.model.Transaction;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(WalletRepository walletRepository,
                                  TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {

        Wallet fromWallet = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new RuntimeException("Source wallet not found"));

        Wallet toWallet = walletRepository.findByUserId(toUserId)
                .orElseThrow(() -> new RuntimeException("Destination wallet not found"));

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // descontar
        fromWallet.withdraw(amount);

        // acreditar
        toWallet.deposit(amount);

        // guardar transacción
        Transaction transaction = new Transaction(
                fromWallet,
                toWallet,
                amount,
                "TRANSFER"
        );

        transactionRepository.save(transaction);
    }
}