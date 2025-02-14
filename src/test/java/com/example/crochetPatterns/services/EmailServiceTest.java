package com.example.crochetPatterns.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {}

    @Test
    @DisplayName("sendConfirmationEmail() - wysyła maila z odpowiednim tematem i treścią")
    void shouldSendConfirmationEmail() {
        String to = "user@example.com";
        String confirmationUrl = "http://example.com/confirm?token=abc";

        emailService.sendConfirmationEmail(to, confirmationUrl);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class); //argument captor do przechwytywania wiadomosci
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sentMsg = captor.getValue();
        assertEquals(to, sentMsg.getTo()[0]);
        assertEquals("Potwierdzenie rejestracji", sentMsg.getSubject());
        assertTrue(sentMsg.getText().contains(confirmationUrl));
    }
}
