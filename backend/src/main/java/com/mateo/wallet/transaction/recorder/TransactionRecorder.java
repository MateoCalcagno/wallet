package com.mateo.wallet.transaction.recorder;

import com.mateo.wallet.transaction.factory.TransactionFactory;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.wallet.model.Wallet;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class TransactionRecorder {

    private final TransactionRepository transactionRepository;
    private final TransactionFactory transactionFactory;

    public TransactionRecorder(TransactionRepository transactionRepository,
                               TransactionFactory transactionFactory) {
        this.transactionRepository = transactionRepository;
        this.transactionFactory = transactionFactory;
    }

    public void recordDeposit(Wallet wallet, BigDecimal amount) {
        transactionRepository.save(transactionFactory.createDeposit(wallet, amount));
    }

    public void recordWithdrawal(Wallet wallet, BigDecimal amount) {
        transactionRepository.save(transactionFactory.createWithdrawal(wallet, amount));
    }
}