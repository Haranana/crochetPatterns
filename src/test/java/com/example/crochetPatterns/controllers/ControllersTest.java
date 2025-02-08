package com.example.crochetPatterns.controllers;


import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.mappers.TagConverter;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.services.CommentService;
import com.example.crochetPatterns.services.PostService;
import com.example.crochetPatterns.services.TagService;
import com.example.crochetPatterns.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import ...
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controllers.class)
class ControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private PostService postService;
    @MockBean
    private PostConverter postConverter;
    @MockBean
    private UserConverter userConverter;
    @MockBean
    private CommentConverter commentConverter;
    @MockBean
    private CommentService commentService;
    @MockBean
    private TagConverter tagConverter;
    @MockBean
    private TagService tagService;
    // (jeśli kontroler wstrzykuje np. userService, to musisz go tu udawać)

    @Test
    void shouldReturnMainMenuView() throws Exception {
        // given
        // np. when(userService....).thenReturn(...)

        // when & then
        mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainMenu"));
        // .andExpect(model().attributeExists("userId")); // np. jeśli to przechodzi w model
    }

    @Test
    void shouldProcessRegistration() throws Exception {
        /*
        mockMvc.perform(post("/register")
                        .param("username", "newUser")
                        .param("email", "new@user.com")
                        .param("password", "abcd")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registrationPending=true"));

         */
    }
}