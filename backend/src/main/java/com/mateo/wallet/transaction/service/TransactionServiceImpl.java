package com.mateo.wallet.transaction.service;

import com.mateo.wallet.common.exception.InsufficientBalanceException;
import com.mateo.wallet.common.exception.ResourceNotFoundException;
import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.model.Transaction;
import com.mateo.wallet.transaction.model.TransactionType;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
    public List<TransactionResponse> getHistory(String email) {
        Wallet wallet = walletRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        return transactionRepository
                .findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(
                    wallet.getId(), wallet.getId()
                )
                .stream()
                .map(t -> {
                    boolean isSender = t.getSourceWallet() != null
                            && t.getSourceWallet().getId().equals(wallet.getId());

                    String direction = isSender ? "SENT" : "RECEIVED";

                    String counterpartEmail = null;
                    if (t.getType() == TransactionType.TRANSFER) {
                        counterpartEmail = isSender
                                ? t.getDestinationWallet().getUser().getEmail()
                                : t.getSourceWallet().getUser().getEmail();
                    }

                    return new TransactionResponse(
                            t.getId(),
                            t.getAmount(),
                            t.getType(),
                            direction,
                            counterpartEmail,
                            t.getCreatedAt()
                    );
                })
                .toList();
    }
}