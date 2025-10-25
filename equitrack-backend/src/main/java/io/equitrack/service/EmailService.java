package io.equitrack.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j  // Add this for logging
public class EmailService {

    private final JavaMailSender mailSender;

    @Async  // This makes it non-blocking!
    public void sendEmail(String to, String subject, String body){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("desiigner4074@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        }catch(Exception e){
            // Don't throw - just log the error so registration still succeeds
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}