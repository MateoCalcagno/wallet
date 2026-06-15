package com.mateo.wallet.transaction.model;

import com.mateo.wallet.common.audit.Auditable;
import com.mateo.wallet.wallet.model.Wallet;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
public class Transaction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_wallet_id", nullable = true) 
    private Wallet sourceWallet;

    @ManyToOne
    @JoinColumn(name = "destination_wallet_id", nullable = true) 
    private Wallet destinationWallet;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    public Transaction() {}

    public Transaction(Wallet sourceWallet,
                       Wallet destinationWallet,
                       BigDecimal amount,
                       TransactionType type) {
        this.sourceWallet = sourceWallet;
        this.destinationWallet = destinationWallet;
        this.amount = amount;
        this.type = type;
    }

    // getters

    public Long getId() { return id; }
    public Wallet getSourceWallet() { return sourceWallet; }
    public Wallet getDestinationWallet() { return destinationWallet; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
}