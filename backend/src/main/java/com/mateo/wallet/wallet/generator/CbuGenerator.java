package com.mateo.wallet.wallet.generator;

import com.mateo.wallet.common.exception.UniqueGenerationException;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.function.Predicate;

@Component
public class CbuGenerator {

    private static final int LENGTH = 22;
    private static final int MAX_ATTEMPTS = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final WalletRepository walletRepository;

    public CbuGenerator(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public String generate() {
        return generateUnique(walletRepository::existsByCbu);
    }

    private String generateUnique(Predicate<String> alreadyExists) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String cbu = generateRandom();
            if (!alreadyExists.test(cbu)) {
                return cbu;
            }
        }
        throw new UniqueGenerationException("CBU");
    }

    private String generateRandom() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(SECURE_RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}