package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // given
        User user = new User();
        user.setUsername("johnDoe");
        user.setEmail("john@example.com"); // @Email
        user.setPassword("secret123");      // @NotEmpty
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Test
    @DisplayName("findUserIdByUsername() - powinno zwrócić ID użytkownika lub null gdy brak")
    void shouldFindUserIdByUsername() {
        // when
        Long id = userRepository.findUserIdByUsername("johnDoe");
        Long unknown = userRepository.findUserIdByUsername("unknownUser");

        // then
        assertNotNull(id);
        assertNull(unknown);
    }

    @Test
    @DisplayName("existsByUsername() - true jeśli istnieje, false jeśli nie")
    void shouldCheckUsernameExists() {
        // when
        boolean ex1 = userRepository.existsByUsername("johnDoe");
        boolean ex2 = userRepository.existsByUsername("nope");

        // then
        assertTrue(ex1);
        assertFalse(ex2);
    }

    @Test
    @DisplayName("existsByEmail() - true jeśli istnieje, false jeśli nie")
    void shouldCheckEmailExists() {
        // when
        boolean ex1 = userRepository.existsByEmail("john@example.com");
        boolean ex2 = userRepository.existsByEmail("random@example.com");

        // then
        assertTrue(ex1);
        assertFalse(ex2);
    }
}
