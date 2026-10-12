package com.mateo.wallet.transaction.model;

import com.mateo.wallet.common.audit.Auditable;
import jakarta.persistence.*;

@Entity
@Table(name = "idempotency_keys",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_idempotency_user_key",
                columnNames = {"user_email", "idempotency_key"}))
public class IdempotencyKey extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "idempotency_key", nullable = false, length = 64)
    private String key;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    public IdempotencyKey() {}

    public IdempotencyKey(String userEmail, String key, String requestHash) {
        this.userEmail = userEmail;
        this.key = key;
        this.requestHash = requestHash;
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getKey() { return key; }
    public String getRequestHash() { return requestHash; }
}