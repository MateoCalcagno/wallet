package com.mateo.wallet.transaction.service;

import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.mapper.TransactionMapper;
import com.mateo.wallet.transaction.model.Transaction;
import com.mateo.wallet.transaction.model.TransactionType;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.resolver.WalletResolver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper; 
    private final WalletResolver walletResolver;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  TransactionMapper transactionMapper,
                                  WalletResolver walletResolver) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.walletResolver = walletResolver;
    }

    @Override
    @Transactional
    public void transfer(String fromEmail, String identifier, BigDecimal amount) {
        Wallet fromWallet = walletResolver.resolveByEmail(fromEmail);
        Wallet toWallet = walletResolver.resolveByIdentifier(identifier);

        if (fromWallet.getId().equals(toWallet.getId())) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }

        fromWallet.withdraw(amount);
        toWallet.deposit(amount);

        transactionRepository.save(new Transaction(fromWallet, toWallet, amount, TransactionType.TRANSFER));
    }

    @Override
    public Page<TransactionResponse> getHistory(String email, int page, int size) {
        Wallet wallet = walletResolver.resolveByEmail(email);

        Pageable pageable = PageRequest.of(page, size);

        return transactionRepository
                .findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(
                        wallet.getId(), wallet.getId(), pageable)
                .map(t -> transactionMapper.toResponse(t, wallet));
    }

    @Override
    @Transactional
    public void registerDeposit(Wallet wallet, BigDecimal amount) {
        transactionRepository.save(new Transaction(null, wallet, amount, TransactionType.DEPOSIT));
    }

    @Override
    @Transactional
    public void registerWithdrawal(Wallet wallet, BigDecimal amount) {
        transactionRepository.save(new Transaction(wallet, null, amount, TransactionType.WITHDRAWAL));
    }
}