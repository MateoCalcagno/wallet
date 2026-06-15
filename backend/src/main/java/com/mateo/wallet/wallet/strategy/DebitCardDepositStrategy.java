package com.mateo.wallet.wallet.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("DEBIT_CARD")
public class DebitCardDepositStrategy implements DepositStrategy {

    private static final BigDecimal COMMISSION = new BigDecimal("0.01"); // 1%

    @Override
    public BigDecimal process(BigDecimal amount) {
        BigDecimal commission = amount.multiply(COMMISSION).setScale(2, RoundingMode.HALF_UP);
        return amount.subtract(commission);
    }
}