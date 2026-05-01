package com.andrei.demo.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private JavaMailSender mailSender;

    public void sendResetCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset Verification Code");
        message.setText("Your verification code is: " + code + "\nValid for 15 minutes.");
        mailSender.send(message);
    }

    public void sendResetConfirmation(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Security Alert: Password Changed");
        message.setText("Hello! This is a confirmation that your password has been successfully updated.");
        mailSender.send(message);
    }
}