package com.mateo.wallet.wallet.strategy;

import java.math.BigDecimal;

public interface DepositStrategy {
    BigDecimal process(BigDecimal amount);
}