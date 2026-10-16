package com.mateo.wallet.wallet.repository;

import com.mateo.wallet.wallet.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
    Optional<Wallet> findByUserEmail(String email);
    Optional<Wallet> findByCbu(String cbu);
    Optional<Wallet> findByAlias(String alias);
    boolean existsByAlias(String alias);
    boolean existsByCbu(String cbu);

    @Query("select w.id from Wallet w where w.user.email = :email")
    Optional<Long> findIdByUserEmail(@Param("email") String email);

    @Query("select w.id from Wallet w where w.cbu = :cbu")
    Optional<Long> findIdByCbu(@Param("cbu") String cbu);

    @Query("select w.id from Wallet w where w.alias = :alias")
    Optional<Long> findIdByAlias(@Param("alias") String alias);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") Long id);
}