package com.example.crochetPatterns.services;

import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.others.LoggedUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    @BeforeEach
    void setUp() {
        // ...
    }

    @Test
    @DisplayName("loadUserByUsername() - gdy użytkownik istnieje -> zwraca LoggedUserDetails")
    void shouldReturnUserDetailsIfUserExists() {
        // given
        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setUsername("john");
        mockUser.setPassword("hashedPass");
        mockUser.setEnabled(true);

        given(userService.getUserByUsername("john")).willReturn(mockUser);

        // when
        var result = myUserDetailsService.loadUserByUsername("john");

        // then
        assertNotNull(result);
        assertTrue(result instanceof LoggedUserDetails);
        LoggedUserDetails lud = (LoggedUserDetails) result;
        assertEquals(10L, lud.getId());
        assertEquals("john", lud.getUsername());
        assertEquals("hashedPass", lud.getPassword());
        assertTrue(lud.isEnabled());
    }

}

