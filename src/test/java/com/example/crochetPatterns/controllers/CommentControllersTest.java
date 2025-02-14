package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.CommentEditDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.repositories.CommentRepository;
import com.example.crochetPatterns.services.CommentService;
import com.example.crochetPatterns.others.LoggedUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@WebMvcTest(CommentControllers.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentConverter commentConverter;
    @MockBean
    private CommentService commentService;
    @MockBean
    private CommentRepository commentRepository;

    @BeforeEach
    void setUpSecurity() {
        LoggedUserDetails principal = new LoggedUserDetails(10L, "testUser", "password", true);

        TestingAuthenticationToken authToken = new TestingAuthenticationToken(principal, null, "ROLE_USER"); // ustawienie token uwierzytelniania z tym principal
        authToken.setAuthenticated(true);

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    @DisplayName("GET /writeComment?postId=10 -> widok writeComment z model atr. postId i authorId")
    void shouldShowWriteCommentForm() throws Exception {
        mockMvc.perform(get("/writeComment").param("postId", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("writeComment"))
                .andExpect(model().attribute("postId", 10))
                .andExpect(model().attribute("authorId", 10L));
    }

    @Test
    @DisplayName("GET /editComment?commentId=5 -> widok editComment z atrybutem commentEditDTO")
    void shouldShowEditCommentForm() throws Exception {

        Comment comment = new Comment();
        comment.setId(5L);
        willReturn(comment).given(commentService).getCommentDTO(5);

        CommentEditDTO dto = new CommentEditDTO();
        dto.setId(5L);
        given(commentConverter.createEditDTOFromComment(comment)).willReturn(dto);


        mockMvc.perform(get("/editComment").param("commentId", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("editComment"))
                .andExpect(model().attribute("commentEditDTO", dto));
    }

    @Test
    @DisplayName("GET /deleteComment?commentId=3&postId=5 -> widok deleteCommentConfirm")
    void shouldShowDeleteCommentConfirm() throws Exception {
        mockMvc.perform(get("/deleteComment")
                        .param("commentId", "3")
                        .param("postId", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("deleteCommentConfirm"))
                .andExpect(model().attribute("commentId", 3))
                .andExpect(model().attribute("postId", 5));
    }
}