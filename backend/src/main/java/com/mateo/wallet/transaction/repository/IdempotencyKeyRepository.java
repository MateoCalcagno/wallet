package com.mateo.wallet.transaction.repository;

import com.mateo.wallet.transaction.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByUserEmailAndKey(String userEmail, String key);
}