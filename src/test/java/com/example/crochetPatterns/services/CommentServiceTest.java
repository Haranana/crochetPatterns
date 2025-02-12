package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.CommentEditDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.exceptions.ElementNotFoundException;
import com.example.crochetPatterns.repositories.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        // ...
    }

    @Test
    @DisplayName("addNewComment() - powinno zapisać w repozytorium")
    void shouldAddNewComment() {
        // given
        Comment comment = new Comment();
        comment.setId(1L);

        // when
        commentService.addNewComment(comment);

        // then
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("findCommentsByIds() - powinno zwrócić listę komentarzy")
    void shouldFindCommentsByIds() {
        // given
        Comment c1 = new Comment();
        c1.setId(1L);
        Comment c2 = new Comment();
        c2.setId(2L);

        given(commentRepository.findAllById(List.of(1L, 2L))).willReturn(Arrays.asList(c1, c2));

        // when
        List<Comment> result = commentService.findCommentsByIds(List.of(1L, 2L));

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(c1));
        assertTrue(result.contains(c2));
    }

    @Test
    @DisplayName("getCommentDTO() - zwraca komentarz jeśli istnieje, wpp. rzuca wyjątek")
    void shouldReturnCommentOrThrow() {
        // given
        Comment c = new Comment();
        c.setId(10L);
        given(commentRepository.findById(10L)).willReturn(Optional.of(c));
        given(commentRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Comment found = commentService.getCommentDTO(10);

        // then
        assertEquals(10L, found.getId());
        assertThrows(ElementNotFoundException.class, () -> commentService.getCommentDTO(999));
    }

    @Test
    @DisplayName("updateExistingComment() - gdy istnieje -> uaktualnia, wpp. wyjątek")
    void shouldUpdateOrThrow() {
        // given
        Comment existing = new Comment();
        existing.setId(5L);
        existing.setText("Old text");

        given(commentRepository.findById(5L)).willReturn(Optional.of(existing));
        given(commentRepository.findById(999L)).willReturn(Optional.empty());

        CommentEditDTO dto = new CommentEditDTO();
        dto.setId(5L);
        dto.setText("New text");

        // when
        commentService.updateExistingComment(dto);

        // then
        assertEquals("New text", existing.getText());
        verify(commentRepository).save(existing);

        // when/then
        CommentEditDTO dto2 = new CommentEditDTO();
        dto2.setId(999L);
        assertThrows(ElementNotFoundException.class, () -> commentService.updateExistingComment(dto2));
    }

    @Test
    @DisplayName("getCommentDTOPageByPost() - powinno wywołać findByPostId(...) z PageRequest")
    void shouldReturnPageByPost() {
        // given
        Page<Comment> mockPage = new PageImpl<>(Collections.emptyList());
        given(commentRepository.findByPostId(eq(10L), any(Pageable.class))).willReturn(mockPage);

        // when
        Page<Comment> result = commentService.getCommentDTOPageByPost(
                0, 5, CommentService.CommentSortType.DEFAULT, 10
        );

        // then
        assertSame(mockPage, result);
        verify(commentRepository).findByPostId(eq(10L), any(Pageable.class));
    }

    @Test
    @DisplayName("getCommentDTOPageByUser() - powinno wywołać findByAuthorId(...) z PageRequest")
    void shouldReturnPageByUser() {
        // given
        Page<Comment> mockPage = new PageImpl<>(Collections.emptyList());
        given(commentRepository.findByAuthorId(eq(77L), any(Pageable.class))).willReturn(mockPage);

        // when
        Page<Comment> result = commentService.getCommentDTOPageByUser(
                1, 10, CommentService.CommentSortType.NEWEST, 77
        );

        // then
        assertSame(mockPage, result);
        verify(commentRepository).findByAuthorId(eq(77L), any(Pageable.class));
    }
}
