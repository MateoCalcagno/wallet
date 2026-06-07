package com.mateo.wallet.transaction.repository;

import com.mateo.wallet.transaction.model.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(
        Long sourceWalletId,
        Long destinationWalletId,
        Pageable pageable
    );
}