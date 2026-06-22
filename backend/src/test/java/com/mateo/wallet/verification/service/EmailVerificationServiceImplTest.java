package com.mateo.wallet.verification.service;

import com.mateo.wallet.common.email.EmailSender;
import com.mateo.wallet.common.exception.ResourceNotFoundException;
import com.mateo.wallet.verification.model.EmailVerification;
import com.mateo.wallet.verification.repository.EmailVerificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {

    @Mock
    private EmailVerificationRepository repo;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    // ─── sendPin ────────────────────────────────────────────────────────────────

    @Test
    void sendPin_deletesExistingAndSavesNewAndSendsEmail() {
        emailVerificationService.sendPin("mateo@gmail.com");

        verify(repo).deleteByEmail("mateo@gmail.com");

        ArgumentCaptor<EmailVerification> captor = ArgumentCaptor.forClass(EmailVerification.class);
        verify(repo).save(captor.capture());

        EmailVerification saved = captor.getValue();
        assertEquals("mateo@gmail.com", saved.getEmail());
        assertNotNull(saved.getPin());
        assertEquals(6, saved.getPin().length());
        assertTrue(saved.getPin().matches("\\d{6}"));
        assertFalse(saved.isVerified());
        assertTrue(saved.getExpiresAt().isAfter(LocalDateTime.now()));

        verify(emailSender).send(eq("mateo@gmail.com"), any(), contains(saved.getPin()));
    }

    @Test
    void sendPin_pinExpiresInApproximately10Minutes() {
        emailVerificationService.sendPin("mateo@gmail.com");

        ArgumentCaptor<EmailVerification> captor = ArgumentCaptor.forClass(EmailVerification.class);
        verify(repo).save(captor.capture());

        LocalDateTime expiresAt = captor.getValue().getExpiresAt();
        LocalDateTime expectedExpiry = LocalDateTime.now().plusMinutes(9); // margen de 1 min

        assertTrue(expiresAt.isAfter(expectedExpiry));
    }

    // ─── verifyPin ──────────────────────────────────────────────────────────────

    @Test
    void verifyPin_success() {
        EmailVerification verification = new EmailVerification(
                "mateo@gmail.com", "123456", LocalDateTime.now().plusMinutes(5));

        when(repo.findTopByEmailOrderByIdDesc("mateo@gmail.com")).thenReturn(Optional.of(verification));

        emailVerificationService.verifyPin("mateo@gmail.com", "123456");

        assertTrue(verification.isVerified());
    }

    @Test
    void verifyPin_noVerificationFound_throwsResourceNotFoundException() {
        when(repo.findTopByEmailOrderByIdDesc("mateo@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                emailVerificationService.verifyPin("mateo@gmail.com", "123456"));
    }

    @Test
    void verifyPin_alreadyVerified_throwsIllegalStateException() {
        EmailVerification verification = new EmailVerification(
                "mateo@gmail.com", "123456", LocalDateTime.now().plusMinutes(5));
        verification.setVerified(true);

        when(repo.findTopByEmailOrderByIdDesc("mateo@gmail.com")).thenReturn(Optional.of(verification));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                emailVerificationService.verifyPin("mateo@gmail.com", "123456"));

        assertEquals("Email already verified", ex.getMessage());
    }

    @Test
    void verifyPin_pinExpired_throwsIllegalStateException() {
        EmailVerification verification = new EmailVerification(
                "mateo@gmail.com", "123456", LocalDateTime.now().minusMinutes(1));

        when(repo.findTopByEmailOrderByIdDesc("mateo@gmail.com")).thenReturn(Optional.of(verification));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                emailVerificationService.verifyPin("mateo@gmail.com", "123456"));

        assertTrue(ex.getMessage().contains("expired"));
    }

    @Test
    void verifyPin_incorrectPin_throwsIllegalArgumentException() {
        EmailVerification verification = new EmailVerification(
                "mateo@gmail.com", "123456", LocalDateTime.now().plusMinutes(5));

        when(repo.findTopByEmailOrderByIdDesc("mateo@gmail.com")).thenReturn(Optional.of(verification));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                emailVerificationService.verifyPin("mateo@gmail.com", "999999"));

        assertEquals("Incorrect PIN", ex.getMessage());
    }

    // ─── assertEmailVerified ─────────────────────────────────────────────────────

    @Test
    void assertEmailVerified_success_deletesVerification() {
        EmailVerification verification = new EmailVerification(
                "mateo@gmail.com", "123456", LocalDateTime.now().plusMinutes(5));
        verification.setVerified(true);

        when(repo.findTopByEmailOrderByIdDesc("mateo@gmail.com")).thenReturn(Optional.of(verification));

        assertDoesNotThrow(() -> emailVerificationService.assertEmailVerified("mateo@gmail.com"));

        verify(repo).deleteByEmail("mateo@gmail.com");
    }

    @Test
    void assertEmailVerified_noVerificationFound_throwsIllegalStateException() {
        when(repo.findTopByEmailOrderByIdDesc("mateo@gmail.com")).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                emailVerificationService.assertEmailVerified("mateo@gmail.com"));

        assertEquals("Email has not been verified", ex.getMessage());
        verify(repo, never()).deleteByEmail(any());
    }

    @Test
    void assertEmailVerified_notVerifiedYet_throwsIllegalStateException() {
        EmailVerification verification = new EmailVerification(
                "mateo@gmail.com", "123456", LocalDateTime.now().plusMinutes(5));

        when(repo.findTopByEmailOrderByIdDesc("mateo@gmail.com")).thenReturn(Optional.of(verification));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                emailVerificationService.assertEmailVerified("mateo@gmail.com"));

        assertTrue(ex.getMessage().contains("verify your email"));
        verify(repo, never()).deleteByEmail(any());
    }
}