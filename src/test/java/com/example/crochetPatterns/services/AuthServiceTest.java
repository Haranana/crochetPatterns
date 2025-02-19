package com.example.crochetPatterns.services;

import com.example.crochetPatterns.others.LoggedUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class AuthServiceTest {

    private final AuthService authService = new AuthService();

    @Test
    @DisplayName("isLogged() - gdy auth != null, isAuthenticated == true i principal to LoggedUserDetails -> true")
    void shouldReturnTrueIfLogged() {
        // given
        Authentication mockAuth = mock(Authentication.class);
        given(mockAuth.isAuthenticated()).willReturn(true);
        LoggedUserDetails principal = new LoggedUserDetails(5L, "user", "pass", true);
        given(mockAuth.getPrincipal()).willReturn(principal);

        SecurityContext mockCtx = mock(SecurityContext.class);
        given(mockCtx.getAuthentication()).willReturn(mockAuth);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(mockCtx);

            boolean result = authService.isLogged();

            assertTrue(result);
        }
    }

    @Test
    @DisplayName("isLogged() - gdy brak auth lub principal nie LoggedUserDetails -> false")
    void shouldReturnFalseIfNotLogged() {

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            SecurityContext mockCtx = mock(SecurityContext.class);
            given(mockCtx.getAuthentication()).willReturn(null);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(mockCtx);

            assertFalse(authService.isLogged());
        }

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            Authentication mockAuth = mock(Authentication.class);
            given(mockAuth.isAuthenticated()).willReturn(true);
            given(mockAuth.getPrincipal()).willReturn("just a string...");

            SecurityContext mockCtx = mock(SecurityContext.class);
            given(mockCtx.getAuthentication()).willReturn(mockAuth);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(mockCtx);

            assertFalse(authService.isLogged());
        }
    }

    @Test
    @DisplayName("getLoggedUserDetails() - zwraca principal jako LoggedUserDetails")
    void shouldReturnLoggedUserDetails() {
        Authentication mockAuth = mock(Authentication.class);
        LoggedUserDetails principal = new LoggedUserDetails(5L, "user", "pass", true);
        given(mockAuth.getPrincipal()).willReturn(principal);

        SecurityContext mockCtx = mock(SecurityContext.class);
        given(mockCtx.getAuthentication()).willReturn(mockAuth);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(mockCtx);

            LoggedUserDetails result = authService.getLoggedUserDetails();

            assertEquals(principal, result);
        }
    }
}
