package com.mateo.wallet.wallet.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class BankTransferDepositStrategy implements DepositStrategy {

    @Override
    public BigDecimal process(BigDecimal amount) {
        return amount;
    }
}