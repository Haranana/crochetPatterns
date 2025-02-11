package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.PostCreateDTO;
import com.example.crochetPatterns.dtos.PostReturnDTO;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.services.TagService;
import com.example.crochetPatterns.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostConverterTest {

    @Mock
    private UserService userService;
    @Mock
    private TagService tagService;

    @InjectMocks
    private PostConverter postConverter;

    @BeforeEach
    void setUp() {
        // Inicjalizacja w razie potrzeby
    }

    @Test
    @DisplayName("Powinien poprawnie stworzyć Post z PostCreateDTO (happy path)")
    void shouldCreatePostFromPostCreateDTO() {
        // given
        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle("Test title");
        dto.setDescription("Desc");
        dto.setAuthorId(10L);
        dto.setTagIds(Set.of(1L, 2L));

        User userMock = new User();
        userMock.setId(10L);

        Tag tag1 = new Tag();
        tag1.setId(1L);
        Tag tag2 = new Tag();
        tag2.setId(2L);

        given(userService.getUser(10L)).willReturn(userMock);
        given(tagService.findTagsByIds(Set.of(1L, 2L))).willReturn(Set.of(tag1, tag2));

        // when
        Post post = postConverter.createPost(dto, "path/to/pdf");

        // then
        assertNotNull(post);
        assertEquals("Test title", post.getTitle());
        assertEquals("Desc", post.getDescription());
        assertEquals(10L, post.getAuthor().getId());
        assertEquals(2, post.getTags().size());
        assertEquals("path/to/pdf", post.getPdfFilePath());
    }

    @Test
    @DisplayName("Powinien utworzyć PostReturnDTO z Post (happy path)")
    void shouldCreateDTOFromPost() {
        // given
        Post post = new Post();
        post.setId(5L);
        post.setTitle("PostTitle");
        post.setDescription("PostDesc");
        post.setPdfFilePath("pdf/path");
        post.setCreationDate(new Timestamp(System.currentTimeMillis()));

        User author = new User();
        author.setId(100L);
        post.setAuthor(author);

        Tag tag = new Tag();
        tag.setId(111L);
        post.setTags(Set.of(tag));

        // when
        PostReturnDTO dto = postConverter.createDTO(post);

        // then
        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("PostTitle", dto.getTitle());
        assertEquals("pdf/path", dto.getPdfFilePath());
        assertEquals(100L, dto.getAuthorId());
        assertEquals(1, dto.getTagIds().size());
    }
}