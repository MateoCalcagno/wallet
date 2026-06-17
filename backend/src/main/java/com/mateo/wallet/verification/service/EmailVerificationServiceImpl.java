package com.mateo.wallet.verification.service;

import com.mateo.wallet.common.exception.ResourceNotFoundException;
import com.mateo.wallet.verification.model.EmailVerification;
import com.mateo.wallet.verification.repository.EmailVerificationRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final int PIN_EXPIRY_MINUTES = 10;

    private final EmailVerificationRepository repo;
    private final JavaMailSender mailSender;

    public EmailVerificationServiceImpl(EmailVerificationRepository repo,
                                        JavaMailSender mailSender) {
        this.repo = repo;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public void sendPin(String email) {
        // Eliminar verificaciones anteriores del mismo email
        repo.deleteByEmail(email);

        String pin = generatePin();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(PIN_EXPIRY_MINUTES);

        repo.save(new EmailVerification(email, pin, expiresAt));

        sendEmail(email, pin);
    }

    @Override
    @Transactional
    public void verifyPin(String email, String pin) {
        EmailVerification verification = repo.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una verificación para ese email"));

        if (verification.isVerified()) {
            throw new IllegalStateException("El email ya fue verificado");
        }
        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            throw new IllegalStateException("El PIN expiró, solicitá uno nuevo");
        }
        if (!verification.getPin().equals(pin)) {
            throw new IllegalArgumentException("PIN incorrecto");
        }

        verification.setVerified(true);
    }

    @Override
    public void assertEmailVerified(String email) {
        EmailVerification verification = repo.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new IllegalStateException("El email no fue verificado"));

        if (!verification.isVerified()) {
            throw new IllegalStateException("Debés verificar tu email antes de registrarte");
        }
    }

    private String generatePin() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void sendEmail(String to, String pin) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Tu código de verificación - Nova Wallet");
        message.setText("Tu código de verificación es: " + pin + "\n\nVence en " + PIN_EXPIRY_MINUTES + " minutos.");
        mailSender.send(message);
    }
}