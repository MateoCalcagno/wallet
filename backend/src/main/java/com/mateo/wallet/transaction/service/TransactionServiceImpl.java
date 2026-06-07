package com.mateo.wallet.transaction.service;

import com.mateo.wallet.common.exception.InsufficientBalanceException;
import com.mateo.wallet.common.exception.ResourceNotFoundException;
import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.model.Transaction;
import com.mateo.wallet.transaction.model.TransactionType;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(WalletRepository walletRepository,
                                  TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

   @Override
    @Transactional
    public void transfer(String fromEmail, String identifier, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Wallet fromWallet = walletRepository.findByUserEmail(fromEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // busca por CBU o alias
        Wallet toWallet = walletRepository.findByCbu(identifier)
                .or(() -> walletRepository.findByAlias(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("Destination wallet not found"));

        if (fromWallet.getId().equals(toWallet.getId())) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        fromWallet.withdraw(amount);
        toWallet.deposit(amount);

        transactionRepository.save(new Transaction(fromWallet, toWallet, amount, TransactionType.TRANSFER));
    }

    @Override
    public Page<TransactionResponse> getHistory(
            String email,
            int page,
            int size
    ) {
        Wallet wallet = walletRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        Pageable pageable = PageRequest.of(page, size);

        return transactionRepository
                .findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(
                        wallet.getId(),
                        wallet.getId(),
                        pageable
                )
                .map(t -> {

                    boolean isSender =
                            t.getSourceWallet() != null &&
                            t.getSourceWallet().getId().equals(wallet.getId());

                    String direction = isSender ? "SENT" : "RECEIVED";

                    String counterpartName = null;

                if (t.getType() == TransactionType.TRANSFER) {
                counterpartName = isSender
                        ? t.getDestinationWallet().getUser().getFirstName()
                                + " "
                                + t.getDestinationWallet().getUser().getLastName()
                        : t.getSourceWallet().getUser().getFirstName()
                                + " "
                                + t.getSourceWallet().getUser().getLastName();
                }

                    return new TransactionResponse(
                            t.getId(),
                            t.getAmount(),
                            t.getType(),
                            direction,
                            counterpartName,
                            t.getCreatedAt()
                    );
                });
    }
}