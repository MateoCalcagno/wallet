package com.mateo.wallet.transaction.service;

import org.springframework.stereotype.Service;

import com.mateo.wallet.common.email.EmailSender;
import com.mateo.wallet.wallet.model.Wallet;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class TransferEmailService {

    private final EmailSender emailSender;

    public TransferEmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void notifyRecipient(Wallet toWallet, BigDecimal amount) {
        emailSender.send(
            toWallet.getUser().getEmail(),
            "Transferencia recibida - NW",
            "Hola " + toWallet.getUser().getFirstName() + ", recibiste " + formatAmount(amount) + " en tu wallet."
        );
    }

    public void notifySender(Wallet fromWallet, BigDecimal amount) {
        emailSender.send(
            fromWallet.getUser().getEmail(),
            "Transferencia enviada - NW",
            "Hola " + fromWallet.getUser().getFirstName() + ", tu transferencia de " + formatAmount(amount) + " fue exitosa."
        );
    }

    private String formatAmount(BigDecimal amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-AR"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return "$" + nf.format(amount);
    }
}