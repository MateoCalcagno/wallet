package com.mateo.wallet.common.email;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    private final Resend resend;

    public EmailSender(@Value("${resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void send(String to, String subject, String body) {
        CreateEmailOptions params = CreateEmailOptions.builder()
            .from("Nova Wallet <onboarding@resend.dev>")
            .to(to)
            .subject(subject)
            .text(body)
            .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            throw new RuntimeException("Error enviando email: " + e.getMessage(), e);
        }
    }
}