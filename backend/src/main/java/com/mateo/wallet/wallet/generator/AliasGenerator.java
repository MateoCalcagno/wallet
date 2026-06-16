package com.mateo.wallet.wallet.generator;

import com.mateo.wallet.common.exception.UniqueGenerationException;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.function.Predicate;

@Component
public class AliasGenerator {

    private static final int MAX_ATTEMPTS = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final String[] WORDS = {
        "sol", "luna", "rio", "mar", "viento", "fuego", "tierra", "cielo",
        "piedra", "flor", "nube", "lluvia", "nieve", "bosque", "lago",
        "campo", "valle", "monte", "arena", "ola", "roca", "hoja",
        "toro", "puma", "aguila", "zorro", "lobo", "tigre", "leon",
        "rosa", "pino", "sauce", "cedro", "roble", "palma", "menta"
    };

    private final WalletRepository walletRepository;

    public AliasGenerator(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public String generate() {
        return generateUnique(walletRepository::existsByAlias);
    }

    private String generateUnique(Predicate<String> alreadyExists) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String alias = generateRandom();
            if (!alreadyExists.test(alias)) {
                return alias;
            }
        }
        throw new UniqueGenerationException("alias");
    }

    private String generateRandom() {
        return WORDS[SECURE_RANDOM.nextInt(WORDS.length)]
             + "." + WORDS[SECURE_RANDOM.nextInt(WORDS.length)]
             + "." + WORDS[SECURE_RANDOM.nextInt(WORDS.length)];
    }
}