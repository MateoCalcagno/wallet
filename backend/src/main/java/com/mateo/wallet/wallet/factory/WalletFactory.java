package com.mateo.wallet.wallet.factory;

import com.mateo.wallet.user.model.User;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.function.Predicate;

@Component
public class WalletFactory {

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

    public WalletFactory(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createForUser(User user) {
        String cbu = generateUniqueCbu(walletRepository::existsByCbu);
        String alias = generateUniqueAlias(walletRepository::existsByAlias);
        return new Wallet(user, cbu, alias);
    }

    private String generateUniqueCbu(Predicate<String> alreadyExists) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String cbu = generateCbu();
            if (!alreadyExists.test(cbu)) {
                return cbu;
            }
        }
        throw new RuntimeException("Could not generate a unique CBU after " + MAX_ATTEMPTS + " attempts");
    }

    private String generateCbu() {
        StringBuilder sb = new StringBuilder(22);
        for (int i = 0; i < 22; i++) {
            sb.append(SECURE_RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private String generateUniqueAlias(Predicate<String> alreadyExists) {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String alias = generateAlias();
            if (!alreadyExists.test(alias)) {
                return alias;
            }
        }
        throw new RuntimeException("Could not generate a unique alias after " + MAX_ATTEMPTS + " attempts");
    }

    private String generateAlias() {
        return WORDS[SECURE_RANDOM.nextInt(WORDS.length)]
             + "." + WORDS[SECURE_RANDOM.nextInt(WORDS.length)]
             + "." + WORDS[SECURE_RANDOM.nextInt(WORDS.length)];
    }
}