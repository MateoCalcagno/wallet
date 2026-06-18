package com.mateo.wallet.transaction.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mateo.wallet.wallet.model.Wallet;
import java.math.BigDecimal;

@Service
public class TransferEmailService {

    private final JavaMailSender mailSender;

    public TransferEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void notifyRecipient(Wallet toWallet, BigDecimal amount) {
        String name = toWallet.getUser().getFirstName();
        String email = toWallet.getUser().getEmail();

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Recibiste una transferencia - Nova Wallet");
        msg.setText("Hola " + name + ", recibiste $" + amount + " en tu wallet.");
        mailSender.send(msg);
    }

    public void notifySender(Wallet fromWallet, BigDecimal amount) {
        String name = fromWallet.getUser().getFirstName();
        String email = fromWallet.getUser().getEmail();

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Transferencia exitosa - Nova Wallet");
        msg.setText("Hola " + name + ", tu transferencia de $" + amount + " fue exitosa.");
        mailSender.send(msg);
    }
}