package com.mateo.wallet.wallet.generator;

import com.mateo.wallet.common.exception.UniqueGenerationException;

public abstract class UniqueGenerator {
    protected static final int MAX_ATTEMPTS = 10;

    protected abstract boolean exists(String value);
    protected abstract String generateRandom();
    protected abstract String entityName(); 

    public String generate() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String value = generateRandom();
            if (!exists(value)) return value;
        }
        throw new UniqueGenerationException(entityName());
    }
}