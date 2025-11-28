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
import sibModel.SendSmtpEmailAttachment;
import java.util.Base64;

import java.util.Base64;
import java.util.Collections;

@Service
@Slf4j  // Logger for tracking email operations
public class EmailService {

    // Inject Brevo (SendinBlue) API key from application.properties
    @Value("${brevo.api.key}")
    private String brevoApiKey;

    /**
     * ASYNCHRONOUSLY SEND EMAIL USING BREVO API
     *
     * This method sends emails in the background without blocking the main application
     * Used for: Account activation, password reset, notifications
     *
     * @param to      - Recipient email address
     * @param subject - Email subject line
     * @param body    - Email content (will be wrapped in HTML)
     */
    @Async  // Runs in background thread - doesn't block user requests
    public void sendEmail(String to, String subject, String body){
        try{
            // --- STEP 1: CONFIGURE BREVO API CLIENT ---
            ApiClient defaultClient = Configuration.getDefaultApiClient();
            ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
            apiKey.setApiKey(brevoApiKey);  // Set API key for authentication

            // --- STEP 2: CREATE EMAIL API INSTANCE ---
            TransactionalEmailsApi api = new TransactionalEmailsApi();

            // --- STEP 3: BUILD EMAIL MESSAGE ---
            SendSmtpEmail email = new SendSmtpEmail();

            // Set sender information
            email.sender(new SendSmtpEmailSender()
                    .name("EquiTrack")              // Display name
                    .email("desiigner4074@gmail.com"));  // From address

            // Set recipient (supports multiple recipients)
            email.to(Collections.singletonList(
                    new SendSmtpEmailTo().email(to)));

            // Set email subject and HTML content
            email.subject(subject);
            email.htmlContent("<html><body><p>" + body + "</p></body></html>");

            // --- STEP 4: SEND EMAIL ---
            api.sendTransacEmail(email);
            log.info("✅ Email sent successfully to: {}", to);

        } catch(Exception e){
            // Log error but don't crash the application
            log.error("❌ Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * NEW METHOD: Send email with Excel attachment
     *
     * @param to       - Recipient email
     * @param subject  - Email subject
     * @param body     - Email body (HTML)
     * @param excelBytes - Excel file as byte array
     * @param filename - Attachment filename
     */
    @Async
    public void sendEmailWithExcel(String to, String subject, String body, byte[] excelBytes, String filename){
        try{
            // Configure Brevo API
            ApiClient defaultClient = Configuration.getDefaultApiClient();
            ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
            apiKey.setApiKey(brevoApiKey);

            TransactionalEmailsApi api = new TransactionalEmailsApi();
            SendSmtpEmail email = new SendSmtpEmail();

            // Set sender
            email.sender(new SendSmtpEmailSender()
                    .name("EquiTrack")
                    .email("desiigner4074@gmail.com"));

            // Set recipient
            email.to(Collections.singletonList(
                    new SendSmtpEmailTo().email(to)));

            // Set subject and body
            email.subject(subject);
            email.htmlContent(body);

            // Add Excel attachment (Base64 encoded bytes)
            SendSmtpEmailAttachment attachment = new SendSmtpEmailAttachment();
            attachment.setName(filename);
            attachment.setContent(Base64.getEncoder().encode(excelBytes)); // ✅ Returns byte[]
            email.setAttachment(Collections.singletonList(attachment));

            // Send email
            api.sendTransacEmail(email);
            log.info("✅ Email with Excel sent successfully to: {}", to);

        } catch(Exception e){
            log.error("❌ Failed to send email with Excel to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}