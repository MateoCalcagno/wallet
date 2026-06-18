package com.mateo.wallet.transaction.service;

import com.mateo.wallet.common.exception.SelfTransferException;
import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.event.TransferCompletedEvent;
import com.mateo.wallet.transaction.factory.TransactionFactory;
import com.mateo.wallet.transaction.mapper.TransactionMapper;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.resolver.WalletResolver;

import org.springframework.context.ApplicationEventPublisher;
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
    private final TransactionFactory transactionFactory;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  TransactionMapper transactionMapper,
                                  WalletResolver walletResolver,
                                  TransactionFactory transactionFactory,
                                  ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.walletResolver = walletResolver;
        this.transactionFactory = transactionFactory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void transfer(String fromEmail, String identifier, BigDecimal amount) {
        Wallet fromWallet = walletResolver.resolveByEmail(fromEmail);
        Wallet toWallet = walletResolver.resolveByIdentifier(identifier);

        if (fromWallet.getId().equals(toWallet.getId())) {
            throw new SelfTransferException();
        }

        fromWallet.withdraw(amount);
        toWallet.deposit(amount);

        transactionRepository.save(transactionFactory.createTransfer(fromWallet, toWallet, amount));
        eventPublisher.publishEvent(new TransferCompletedEvent(fromWallet, toWallet, amount));
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
}