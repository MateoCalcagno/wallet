package com.mateo.wallet.wallet.strategy;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DepositStrategyFactory {

    private final Map<String, DepositStrategy> strategies;

    public DepositStrategyFactory(Map<String, DepositStrategy> strategies) {
        this.strategies = strategies;
    }

    public DepositStrategy getStrategy(PaymentMethod method) {
        DepositStrategy strategy = strategies.get(method.name());
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for payment method: " + method);
        }
        return strategy;
    }
}