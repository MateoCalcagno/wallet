package com.mateo.wallet.wallet.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component("BANK_TRANSFER")
public class BankTransferDepositStrategy implements DepositStrategy {

    @Override
    public BigDecimal process(BigDecimal amount) {
        return amount;
    }
}