package com.mateo.wallet.wallet.generator;

import com.mateo.wallet.wallet.repository.WalletRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CbuGenerator extends UniqueGenerator {

    private static final int LENGTH = 22;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final WalletRepository walletRepository;

    public CbuGenerator(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    protected boolean exists(String value) {
        return walletRepository.existsByCbu(value);
    }

    @Override
    protected String generateRandom() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(SECURE_RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    protected String entityName() {
        return "CBU";
    }
}