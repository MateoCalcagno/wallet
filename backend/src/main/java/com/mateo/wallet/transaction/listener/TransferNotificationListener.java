package com.mateo.wallet.transaction.listener;

import com.mateo.wallet.transaction.email.TransferEmailService;
import com.mateo.wallet.transaction.event.TransferCompletedEvent;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TransferNotificationListener {

    private final TransferEmailService transferEmailService;

    public TransferNotificationListener(TransferEmailService transferEmailService) {
        this.transferEmailService = transferEmailService;
    }

    @Async
    @EventListener
    public void onTransferCompleted(TransferCompletedEvent event) {
        transferEmailService.notifyRecipient(event.toWallet(), event.amount());
        transferEmailService.notifySender(event.fromWallet(), event.amount());
    }
}