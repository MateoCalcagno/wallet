package com.mateo.wallet.transaction.model;

import com.mateo.wallet.wallet.model.Wallet;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_wallet_id")
    private Wallet sourceWallet;

    @ManyToOne
    @JoinColumn(name = "destination_wallet_id")
    private Wallet destinationWallet;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Transaction() {
    }

    public Transaction(Wallet sourceWallet,
                       Wallet destinationWallet,
                       BigDecimal amount,
                       TransactionType type) {
        this.sourceWallet = sourceWallet;
        this.destinationWallet = destinationWallet;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // getters

    public Long getId() {
        return id;
    }
}