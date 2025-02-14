package com.example.crochetPatterns.services;



import com.example.crochetPatterns.dtos.CommentCreateDTO;
import com.example.crochetPatterns.dtos.CommentReturnDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.services.PostService;
import com.example.crochetPatterns.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommentConverterTest {

    @Mock
    private PostService postService;
    @Mock
    private UserService userService;

    @InjectMocks
    private CommentConverter commentConverter;

    @BeforeEach
    void setUp() {}

    @Test
    @DisplayName("Powinien utworzyć Comment z CommentReturnDTO")
    void shouldCreateCommentFromCommentReturnDTO() {

        CommentReturnDTO dto = new CommentReturnDTO();
        dto.setText("Text of comment");
        dto.setAuthorId(10L);
        dto.setPostId(20L);

        User mockUser = new User();
        mockUser.setId(10L);

        Post mockPost = new Post();
        mockPost.setId(20L);

        given(userService.getUserOrNull(10L)).willReturn(mockUser);
        given(postService.getPost(20)).willReturn(mockPost);


        Comment comment = commentConverter.createComment(dto);


        assertNotNull(comment);
        assertEquals("Text of comment", comment.getText());
        assertEquals(10L, comment.getAuthor().getId());
        assertEquals(20L, comment.getPost().getId());
    }

    @Test
    @DisplayName("Powinien utworzyć Comment z CommentCreateDTO")
    void shouldCreateCommentFromCommentCreateDTO() {

        CommentCreateDTO dto = new CommentCreateDTO();
        dto.setText("Another comment");
        dto.setAuthorId(5L);
        dto.setPostId(7L);

        User mockUser = new User();
        mockUser.setId(5L);

        Post mockPost = new Post();
        mockPost.setId(7L);

        given(userService.getUserOrNull(5L)).willReturn(mockUser);
        given(postService.getPost(7)).willReturn(mockPost);


        Comment comment = commentConverter.createComment(dto);


        assertNotNull(comment);
        assertEquals("Another comment", comment.getText());
        assertEquals(5L, comment.getAuthor().getId());
        assertEquals(7L, comment.getPost().getId());
    }

    @Test
    @DisplayName("Powinien utworzyć CommentReturnDTO z Comment")
    void shouldCreateDTOFromComment() {

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("Test comment");
        comment.setCreationDate(new Timestamp(System.currentTimeMillis()));
        Post post = new Post();
        post.setId(999L);
        comment.setPost(post);

        User author = new User();
        author.setId(123L);
        comment.setAuthor(author);


        CommentReturnDTO dto = commentConverter.createDTO(comment);


        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals("Test comment", dto.getText());
        assertEquals(999L, dto.getPostId());
        assertEquals(123L, dto.getAuthorId());

    }
}