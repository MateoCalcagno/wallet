package com.mateo.wallet.verification.service;

import com.mateo.wallet.common.email.EmailSender;
import com.mateo.wallet.common.exception.ResourceNotFoundException;
import com.mateo.wallet.common.exception.TooManyRequestsException;
import com.mateo.wallet.verification.model.EmailVerification;
import com.mateo.wallet.verification.repository.EmailVerificationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final int PIN_EXPIRY_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EmailVerificationRepository repo;
    private final EmailSender emailSender;

    public EmailVerificationServiceImpl(EmailVerificationRepository repo,
                                        EmailSender emailSender) {
        this.repo = repo;
        this.emailSender = emailSender;
    }

    @Override
    @Transactional
    public void sendPin(String email) {
        repo.findTopByEmailOrderByIdDesc(email).ifPresent(existing -> {
            LocalDateTime sentAt = existing.getExpiresAt().minusMinutes(PIN_EXPIRY_MINUTES);
            if (LocalDateTime.now().isBefore(sentAt.plusSeconds(RESEND_COOLDOWN_SECONDS))) {
                throw new TooManyRequestsException("Please wait a minute before requesting a new PIN");
            }
        });

        repo.deleteByEmail(email);

        String pin = generatePin();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(PIN_EXPIRY_MINUTES);

        repo.save(new EmailVerification(email, pin, expiresAt));

        sendEmail(email, pin);
    }

    // noRollbackFor: si el PIN es incorrecto se lanza la excepción, pero el
    // intento fallido tiene que quedar guardado. Sin esto, el rollback lo borraría.
    @Override
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void verifyPin(String email, String pin) {
        EmailVerification verification = repo.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new ResourceNotFoundException("No verification found for this email"));

        if (verification.isVerified()) {
            throw new IllegalStateException("Email already verified");
        }
        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            throw new IllegalStateException("PIN has expired, please request a new one");
        }
        if (verification.getAttempts() >= MAX_ATTEMPTS) {
            throw new TooManyRequestsException("Too many incorrect attempts, please request a new PIN");
        }
        if (!verification.getPin().equals(pin)) {
            verification.incrementAttempts();
            throw new IllegalArgumentException("Incorrect PIN");
        }

        verification.setVerified(true);
    }

    @Override
    @Transactional
    public void assertEmailVerified(String email) {
        EmailVerification verification = repo.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new IllegalStateException("Email has not been verified"));

        if (!verification.isVerified()) {
            throw new IllegalStateException("You must verify your email before continuing");
        }

        repo.deleteByEmail(email);
    }

    private String generatePin() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private void sendEmail(String to, String pin) {
        emailSender.send(
            to,
            "Tu código de verificación - NW",
            "Tu código de verificación es: " + pin + "\n\nVence en " + PIN_EXPIRY_MINUTES + " minutos."
        );
    }
}