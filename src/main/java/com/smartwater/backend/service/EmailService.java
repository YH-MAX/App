// EmailService.java
package com.smartwater.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.verify-email.base-url}")
    private String verifyEmailBaseUrl;

    @Value("${app.reset-password.base-url}")
    private String resetPasswordBaseUrl;


    public void sendVerificationEmail(String toEmail, String token) {
        String verifyLink = verifyEmailBaseUrl + "?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("SmartWater - Verify your email");

        String text = "Hi,\n\n"
                + "Thank you for registering SmartWater.\n"
                + "Please click the link below to verify your email address:\n\n"
                + verifyLink + "\n\n"
                + "If you did not sign up, please ignore this email.\n\n"
                + "SmartWater Team";

        message.setText(text);
        mailSender.send(message);
    }


    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = resetPasswordBaseUrl + "?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("SmartWater - Reset your password");

        String text = "Hi,\n\n"
                + "We received a request to reset your SmartWater password.\n"
                + "Click the link below to set a new password:\n\n"
                + resetLink + "\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "SmartWater Team";

        message.setText(text);
        mailSender.send(message);
    }
}
