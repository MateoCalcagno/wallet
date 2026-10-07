package com.mateo.wallet.transaction.listener;

import com.mateo.wallet.transaction.event.TransferCompletedEvent;
import com.mateo.wallet.transaction.service.TransferEmailService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransferNotificationListener {

    private final TransferEmailService transferEmailService;

    public TransferNotificationListener(TransferEmailService transferEmailService) {
        this.transferEmailService = transferEmailService;
    }

    // AFTER_COMMIT: el mail sale solo si la transferencia quedó confirmada en la base.
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferCompleted(TransferCompletedEvent event) {
        transferEmailService.notifyRecipient(event.toWallet(), event.amount());
        transferEmailService.notifySender(event.fromWallet(), event.amount());
    }
}