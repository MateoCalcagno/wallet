package com.mateo.wallet.wallet.repository;

import com.mateo.wallet.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
    Optional<Wallet> findByUserEmail(String email);
    Optional<Wallet> findByCbu(String cbu);
    Optional<Wallet> findByAlias(String alias);
    boolean existsByAlias(String alias);
}