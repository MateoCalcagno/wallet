package com.mateo.wallet.transaction.factory;

import com.mateo.wallet.transaction.model.Transaction;
import com.mateo.wallet.transaction.model.TransactionType;
import com.mateo.wallet.wallet.model.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionFactory {

    public Transaction createTransfer(Wallet from, Wallet to, BigDecimal amount) {
        return new Transaction(from, to, amount, TransactionType.TRANSFER);
    }

    public Transaction createDeposit(Wallet wallet, BigDecimal amount) {
        return new Transaction(null, wallet, amount, TransactionType.DEPOSIT);
    }

    public Transaction createWithdrawal(Wallet wallet, BigDecimal amount) {
        return new Transaction(wallet, null, amount, TransactionType.WITHDRAWAL);
    }
}