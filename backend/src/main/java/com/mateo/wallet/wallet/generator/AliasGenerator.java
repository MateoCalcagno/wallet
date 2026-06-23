package com.mateo.wallet.wallet.generator;

import com.mateo.wallet.wallet.repository.WalletRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AliasGenerator extends UniqueGenerator {

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

    @Override
    protected boolean exists(String value) {
        return walletRepository.existsByAlias(value);
    }

    @Override
    protected String generateRandom() {
        return WORDS[SECURE_RANDOM.nextInt(WORDS.length)]
             + "." + WORDS[SECURE_RANDOM.nextInt(WORDS.length)]
             + "." + WORDS[SECURE_RANDOM.nextInt(WORDS.length)];
    }

    @Override
    protected String entityName() {
        return "alias";
    }
}