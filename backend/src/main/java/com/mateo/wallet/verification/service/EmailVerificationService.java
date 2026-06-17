package com.mateo.wallet.verification.service;

public interface EmailVerificationService {
    void sendPin(String email);
    void verifyPin(String email, String pin);
    void assertEmailVerified(String email);
}