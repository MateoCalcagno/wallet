package com.mateo.wallet.verification.repository;

import com.mateo.wallet.verification.model.EmailVerification;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // Con lock: dos intentos simultáneos no pueden leer el mismo contador
    // y saltarse el límite de intentos.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EmailVerification> findTopByEmailOrderByIdDesc(String email);

    void deleteByEmail(String email);
}