package com.mateo.wallet.wallet.model;

import com.mateo.wallet.common.exception.InsufficientBalanceException;
import com.mateo.wallet.user.model.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
public class Wallet {

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

    public Wallet(User user, String cbu, String alias) {
        this.user = user;
        this.balance = BigDecimal.ZERO;
        this.cbu = cbu;
        this.alias = alias;
        this.createdAt = LocalDateTime.now();
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        this.balance = this.balance.subtract(amount);
    }

    public void setAlias(String alias) { this.alias = alias; }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public BigDecimal getBalance() { return balance; }
    public String getCbu() { return cbu; }
    public String getAlias() { return alias; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}