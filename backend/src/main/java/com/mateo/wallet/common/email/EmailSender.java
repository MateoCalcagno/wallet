package com.mateo.wallet.common.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.List;

@Component
public class EmailSender {

    private final TransactionalEmailsApi api;
    private final String senderEmail;
    private final String senderName;

    public EmailSender(
            @Value("${brevo.api-key}") String apiKey,
            @Value("${brevo.sender-email}") String senderEmail,
            @Value("${brevo.sender-name}") String senderName) {
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        ApiClient client = Configuration.getDefaultApiClient();
        ApiKeyAuth auth = (ApiKeyAuth) client.getAuthentication("api-key");
        auth.setApiKey(apiKey);
        this.api = new TransactionalEmailsApi();
    }

    public void send(String to, String subject, String body) {
        SendSmtpEmail email = new SendSmtpEmail();
        email.setTo(List.of(new SendSmtpEmailTo().email(to)));
        email.setSubject(subject);
        email.setTextContent(body);
        email.setSender(new SendSmtpEmailSender()
            .name(senderName)
            .email(senderEmail));

        try {
            api.sendTransacEmail(email);
        } catch (ApiException e) {
            throw new RuntimeException("Error enviando email: " + e.getMessage(), e);
        }
    }
}