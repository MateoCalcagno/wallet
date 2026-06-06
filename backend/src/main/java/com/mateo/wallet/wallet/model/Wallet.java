package com.mateo.wallet.wallet.model;

import com.mateo.wallet.user.model.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "wallets")
public class Wallet {

    private static final String[] WORDS = {
        "sol", "luna", "rio", "mar", "viento", "fuego", "tierra", "cielo",
        "piedra", "flor", "nube", "lluvia", "nieve", "bosque", "lago",
        "campo", "valle", "monte", "arena", "ola", "roca", "hoja",
        "toro", "puma", "aguila", "zorro", "lobo", "tigre", "leon",
        "rosa", "pino", "sauce", "cedro", "roble", "palma", "menta"
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false, unique = true, length = 22)
    private String cbu;

    @Column(unique = true)
    private String alias;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Wallet() {}

    public Wallet(User user) {
        this.user = user;
        this.balance = BigDecimal.ZERO;
        this.cbu = generateCbu();
        this.alias = generateAlias();
        this.createdAt = LocalDateTime.now();
    }

    private String generateCbu() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(22);
        for (int i = 0; i < 22; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateAlias() {
        Random random = new Random();
        String w1 = WORDS[random.nextInt(WORDS.length)];
        String w2 = WORDS[random.nextInt(WORDS.length)];
        String w3 = WORDS[random.nextInt(WORDS.length)];
        return w1 + "." + w2 + "." + w3;
    }

    public void regenerateAlias() {
        this.alias = generateAlias();
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public BigDecimal getBalance() { return balance; }
    public String getCbu() { return cbu; }
    public String getAlias() { return alias; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}