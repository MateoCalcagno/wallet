package com.mateo.wallet.transaction.mapper;

import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.model.Transaction;
import com.mateo.wallet.transaction.model.TransactionType;
import com.mateo.wallet.wallet.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction t, Wallet wallet) {
        boolean isSender = t.getSourceWallet() != null &&
                t.getSourceWallet().getId().equals(wallet.getId());

        String direction = isSender ? "SENT" : "RECEIVED";

        String counterpartName = null;
        if (t.getType() == TransactionType.TRANSFER) {
            counterpartName = isSender
                    ? t.getDestinationWallet().getUser().getFirstName()
                            + " " + t.getDestinationWallet().getUser().getLastName()
                    : t.getSourceWallet().getUser().getFirstName()
                            + " " + t.getSourceWallet().getUser().getLastName();
        }

        return new TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getType(),
                direction,
                counterpartName,
                t.getCreatedAt()
        );
    }
}