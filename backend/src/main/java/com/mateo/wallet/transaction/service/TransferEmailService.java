package com.mateo.wallet.transaction.service;

import org.springframework.stereotype.Service;

import com.mateo.wallet.common.email.EmailSender;
import com.mateo.wallet.wallet.model.Wallet;
import java.math.BigDecimal;

@Service
public class TransferEmailService {

    private final EmailSender emailSender;

    public TransferEmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void notifyRecipient(Wallet toWallet, BigDecimal amount) {
        emailSender.send(
            toWallet.getUser().getEmail(),
            "Recibiste una transferencia - Nova Wallet",
            "Hola " + toWallet.getUser().getFirstName() + ", recibiste $" + amount + " en tu wallet."
        );
    }

    public void notifySender(Wallet fromWallet, BigDecimal amount) {
        emailSender.send(
            fromWallet.getUser().getEmail(),
            "Transferencia exitosa - Nova Wallet",
            "Hola " + fromWallet.getUser().getFirstName() + ", tu transferencia de $" + amount + " fue exitosa."
        );
    }
}