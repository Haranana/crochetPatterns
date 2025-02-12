package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.entities.VerificationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VerificationTokenRepositoryTest {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // given
        User user = new User();
        user.setUsername("jane");
        user.setEmail("jane@example.com");
        user.setPassword("passwordXYZ");
        user.setEnabled(true);
        userRepository.save(user);

        VerificationToken token = new VerificationToken();
        token.setToken("ABC123");
        token.setUser(user);
        token.setExpiryDate(Timestamp.from(Instant.now().plusSeconds(3600))); // np. +1h
        verificationTokenRepository.save(token);
    }

    @Test
    @DisplayName("findByToken() - istniejący token -> Optional z obiektem, brak -> Optional.empty()")
    void shouldFindByToken() {
        // when
        Optional<VerificationToken> found = verificationTokenRepository.findByToken("ABC123");
        Optional<VerificationToken> notFound = verificationTokenRepository.findByToken("XYZ999");

        // then
        assertTrue(found.isPresent());
        assertEquals("ABC123", found.get().getToken());

        assertTrue(notFound.isEmpty());
    }
}
