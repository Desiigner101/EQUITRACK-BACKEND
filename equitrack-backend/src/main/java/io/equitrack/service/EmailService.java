package io.equitrack.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.Collections;

@Service
@Slf4j
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Async
    public void sendEmail(String to, String subject, String body){
        try{
            // Configure Brevo API client
            ApiClient defaultClient = Configuration.getDefaultApiClient();
            ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
            apiKey.setApiKey(brevoApiKey);

            // Create the API instance
            TransactionalEmailsApi api = new TransactionalEmailsApi();

            // Build the email
            SendSmtpEmail email = new SendSmtpEmail();

            // Set sender
            email.sender(new SendSmtpEmailSender()
                    .name("EquiTrack")
                    .email("desiigner4074@gmail.com"));

            // Set recipient
            email.to(Collections.singletonList(
                    new SendSmtpEmailTo().email(to)));

            // Set subject and content
            email.subject(subject);
            email.htmlContent("<html><body><p>" + body + "</p></body></html>");

            // Send the email
            api.sendTransacEmail(email);
            log.info("✅ Email sent successfully to: {}", to);

        } catch(Exception e){
            log.error("❌ Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}