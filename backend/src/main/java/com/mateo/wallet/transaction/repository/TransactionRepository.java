package com.mateo.wallet.transaction.repository;

import com.mateo.wallet.transaction.model.Transaction;
import com.mateo.wallet.transaction.model.TransactionType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(
        value = "SELECT t FROM Transaction t WHERE t.sourceWallet.id = ?1 OR t.destinationWallet.id = ?2 ORDER BY t.createdAt DESC",
        countQuery = "SELECT COUNT(t) FROM Transaction t WHERE t.sourceWallet.id = ?1 OR t.destinationWallet.id = ?2"
    )
    Page<Transaction> findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(
        Long sourceWalletId,
        Long destinationWalletId,
        Pageable pageable
    );

    @Query("SELECT t FROM Transaction t WHERE " +
        "(t.sourceWallet.id = :walletId OR t.destinationWallet.id = :walletId) " +
        "AND t.type = :type " +
        "ORDER BY t.createdAt DESC")
    Page<Transaction> findByWalletIdAndType(
        @Param("walletId") Long walletId,
        @Param("type") TransactionType type,
        Pageable pageable
    );
}