package com.example.crochetPatterns.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String confirmationUrl) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject("Potwierdzenie rejestracji");
        mailMessage.setText("Aby aktywować konto, kliknij: " + confirmationUrl);
        mailSender.send(mailMessage);
    }
}
