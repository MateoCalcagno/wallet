package com.mateo.wallet.wallet.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CreditCardDepositStrategy implements DepositStrategy {

    private static final BigDecimal COMMISSION = new BigDecimal("0.03"); // 3%

    @Override
    public BigDecimal process(BigDecimal amount) {
        BigDecimal commission = amount.multiply(COMMISSION).setScale(2, RoundingMode.HALF_UP);
        return amount.subtract(commission);
    }
}