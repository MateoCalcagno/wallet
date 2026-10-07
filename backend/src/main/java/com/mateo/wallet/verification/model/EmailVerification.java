package com.mateo.wallet.verification.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean verified = false;

    // columnDefinition con default: así Hibernate puede agregar la columna
    // aunque la tabla ya tenga filas (en Render).
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int attempts = 0;

    public EmailVerification() {}

    public EmailVerification(String email, String pin, LocalDateTime expiresAt) {
        this.email = email;
        this.pin = pin;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPin() { return pin; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public int getAttempts() { return attempts; }
    public void incrementAttempts() { this.attempts++; }
}