package com.mateo.wallet.transaction.mapper;

import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.model.Transaction;
import com.mateo.wallet.transaction.model.TransactionType;
import com.mateo.wallet.wallet.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction t, Wallet wallet) {
        boolean isSender = isSender(t, wallet);
        String direction = resolveDirection(isSender);
        String counterpartName = resolveCounterpartName(t, isSender);

        return new TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getType(),
                direction,
                counterpartName,
                t.getCreatedAt()
        );
    }

    private boolean isSender(Transaction t, Wallet wallet) {
        return t.getSourceWallet() != null &&
                t.getSourceWallet().getId().equals(wallet.getId());
    }

    private String resolveDirection(boolean isSender) {
        return isSender ? "SENT" : "RECEIVED";
    }

    private String resolveCounterpartName(Transaction t, boolean isSender) {
        if (t.getType() != TransactionType.TRANSFER) {
            return null;
        }

        Wallet counterpart = isSender ? t.getDestinationWallet() : t.getSourceWallet();
        return counterpart.getUser().getFirstName() + " " + counterpart.getUser().getLastName();
    }
}