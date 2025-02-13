package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.UserRegistrationDTO;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.entities.VerificationToken;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.repositories.UserRepository;
import com.example.crochetPatterns.repositories.VerificationTokenRepository;
import com.example.crochetPatterns.services.AuthService;
import com.example.crochetPatterns.services.EmailService;
import com.example.crochetPatterns.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@WebMvcTest(AuthControllers.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private VerificationTokenRepository verificationTokenRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AuthService authService;
    @MockBean
    private EmailService emailService;
    @MockBean
    private UserConverter userConverter;

    @Test
    @DisplayName("GET /login -> widok login")
    void shouldShowLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("GET /register -> widok register z pustym userRegistrationDTO")
    void shouldShowRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userRegistrationDTO"));
    }

    @Test
    @DisplayName("GET /confirm?token=ABC - gdy token poprawny i nie wygasł -> logika aktywuje usera i usuwa token, widok login z message")
    void shouldConfirmRegistration() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setEnabled(false);

        VerificationToken vt = new VerificationToken();
        vt.setToken("ABC");
        vt.setUser(user);
        vt.setExpiryDate(Timestamp.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)));

        given(verificationTokenRepository.findByToken("ABC")).willReturn(Optional.of(vt));

        // when & then
        mockMvc.perform(get("/confirm").param("token","ABC"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

        verify(userRepository).save(user);
        verify(verificationTokenRepository).delete(vt);
    }

    @Test
    @DisplayName("GET /confirm?token=WRONG - gdy token nieznany -> widok error z message")
    void shouldShowErrorForUnknownToken() throws Exception {
        // given
        given(verificationTokenRepository.findByToken("WRONG")).willReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/confirm").param("token","WRONG"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }
}
