package com.mateo.wallet.wallet.service;

import com.mateo.wallet.wallet.dto.WalletResponse;
import com.mateo.wallet.wallet.factory.WalletFactory;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.transaction.recorder.TransactionRecorder;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.wallet.repository.WalletRepository;
import com.mateo.wallet.wallet.resolver.WalletResolver;
import com.mateo.wallet.wallet.strategy.DepositStrategy;
import com.mateo.wallet.wallet.strategy.DepositStrategyFactory;
import com.mateo.wallet.wallet.strategy.PaymentMethod;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRecorder transactionRecorder;
    private final WalletResolver walletResolver;
    private final WalletFactory walletFactory;
    private final DepositStrategyFactory depositStrategyFactory;

    public WalletServiceImpl(WalletRepository walletRepository,
                            TransactionRecorder transactionRecorder,
                            WalletResolver walletResolver,
                            WalletFactory walletFactory,
                            DepositStrategyFactory depositStrategyFactory) {
        this.walletRepository = walletRepository;
        this.transactionRecorder = transactionRecorder;
        this.walletResolver = walletResolver;
        this.walletFactory = walletFactory;
        this.depositStrategyFactory = depositStrategyFactory;
    }

    @Override
    public WalletResponse getBalance(String email) {
        Wallet wallet = walletResolver.resolveByEmail(email);
        return new WalletResponse(wallet.getBalance(), wallet.getCbu(), wallet.getAlias());
    }

    @Override
    @Transactional
    public void deposit(String email, BigDecimal amount, PaymentMethod paymentMethod) {
        Wallet wallet = walletResolver.resolveByEmail(email);

        DepositStrategy strategy = depositStrategyFactory.getStrategy(paymentMethod);
        BigDecimal finalAmount = strategy.process(amount);

        wallet.deposit(finalAmount);
        transactionRecorder.recordDeposit(wallet, finalAmount);
    }

    @Override
    @Transactional
    public void withdraw(String email, BigDecimal amount) {
        Wallet wallet = walletResolver.resolveByEmail(email);
        wallet.withdraw(amount);
        transactionRecorder.recordWithdrawal(wallet, amount);
    }

    @Override
    @Transactional
    public void updateAlias(String email, String alias) {
        Wallet wallet = walletResolver.resolveByEmail(email);

        if (wallet.getAlias().equals(alias)) {
            return; // ya tiene ese alias, no hay nada que cambiar
        }

        if (walletRepository.existsByAlias(alias)) {
            throw new IllegalArgumentException("Alias already in use");
        }

        wallet.setAlias(alias);
    }

    @Override
    @Transactional
    public Wallet createForUser(User user) {
        Wallet wallet = walletFactory.createForUser(user);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet getByUserId(Long userId) {
        return walletResolver.resolveByUserId(userId);
    }
}