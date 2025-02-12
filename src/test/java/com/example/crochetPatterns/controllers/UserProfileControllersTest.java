package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.UserReturnDTO;
import com.example.crochetPatterns.services.*;
import com.example.crochetPatterns.mappers.*;
import com.example.crochetPatterns.others.LoggedUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(UserProfileControllers.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserProfileControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;
    @MockBean
    private PostConverter postConverter;
    @MockBean
    private UserConverter userConverter;
    @MockBean
    private UserService userService;
    @MockBean
    private CommentConverter commentConverter;
    @MockBean
    private CommentService commentService;
    @MockBean
    private AuthService authService;
    @MockBean
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        // ...
    }

    @Test
    @DisplayName("/myProfile - gdy użytkownik zalogowany -> widok showUserProfile")
    void shouldShowUserProfileWhenLogged() throws Exception {
        // given
        given(authService.isLogged()).willReturn(true);
        // Zakładamy, że user ma ID=5
        LoggedUserDetails loggedUser = new LoggedUserDetails(5L, "john", "secret", true);
        given(authService.getLoggedUserDetails()).willReturn(loggedUser);

        UserReturnDTO userDto = new UserReturnDTO();
        userDto.setId(5L);
        userDto.setUsername("john");
        given(userService.getUser(eq(5L))).willReturn(null); // w sumie userService.getUser() przyjmuje int, ale na wszelki wypadek...
        // ewentualnie:
        willReturn(null).given(userService).getUser(anyInt());
        given(userConverter.createDTO(any())).willReturn(userDto);

        // when & then
        mockMvc.perform(get("/myProfile"))
                .andExpect(status().isOk())
                .andExpect(view().name("showUserProfile"))
                .andExpect(model().attribute("user", userDto))
                .andExpect(model().attribute("isViewedByAuthor", true));
    }

    @Test
    @DisplayName("/myProfile - gdy użytkownik niezalogowany -> redirect do login")
    void shouldRedirectToLoginIfNotLogged() throws Exception {
        // given
        given(authService.isLogged()).willReturn(false);

        // when & then
        mockMvc.perform(get("/myProfile"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("/userProfile?userId=X - sprawdzanie isViewedByAuthor")
    void shouldShowUserProfileWithIsViewedByAuthor() throws Exception {
        // given
        // scenariusz 1: zalogowany user (ID=10), param userId=10 => isViewedByAuthor=true
        UserReturnDTO userDto = new UserReturnDTO();
        userDto.setId(10L);
        userDto.setUsername("alice");

        given(userService.getUser(10)).willReturn(null);
        given(userConverter.createDTO(any())).willReturn(userDto);

        given(authService.isLogged()).willReturn(true);
        LoggedUserDetails logDet = new LoggedUserDetails(10L, "alice", "xxx", true);
        given(authService.getLoggedUserDetails()).willReturn(logDet);

        // when & then
        mockMvc.perform(get("/userProfile").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("showUserProfile"))
                .andExpect(model().attribute("user", userDto))
                .andExpect(model().attribute("isViewedByAuthor", true));
    }
}
