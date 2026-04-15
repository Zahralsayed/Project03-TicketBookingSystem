package com.ga.TicketSystem.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationLink(String to, String username, String token) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            String url = "http://localhost:8080/auth/users/verify?token=" + token;

            helper.setTo(to);
            helper.setSubject("Complete Your Registration");
            helper.setText("Hello " +username + ", Please Click the link to verify your email: " + url);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Email sending failed", e);
        }
    }

    public void sendResetLink(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // This points to your NEW reset endpoint
            String url = "http://localhost:8080/auth/users/reset-password?token=" + token;

            helper.setTo(to);
            helper.setSubject(" Ticket System - Password Reset Request");

            // Using HTML 'true' makes the link clickable in most email clients
            String content = "<p>Hello,</p>" +
                    "<p>You requested to reset your password. Click the link below to proceed:</p>" +
                    "<a href=\"" + url + "\">Reset My Password</a>" +
                    "<p>If you did not request this, please ignore this email.</p>";

            helper.setText(content, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

}
