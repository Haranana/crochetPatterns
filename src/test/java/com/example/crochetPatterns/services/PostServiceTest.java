package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.PostCreateDTO;
import com.example.crochetPatterns.dtos.PostEditDTO;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.exceptions.ElementNotFoundException;
import com.example.crochetPatterns.exceptions.FileStorageException;
import com.example.crochetPatterns.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagService tagService;

    @Mock
    private LikeService likeService;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        // Inicjalizacja jeśli potrzebna
    }

    @Test
    @DisplayName("addNewPost() - powinno wywołać save() w repozytorium")
    void shouldSavePost() {
        // given
        Post post = new Post();
        post.setId(1L);

        // when
        postService.addNewPost(post);

        // then
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("deletePost() - gdy istnieje -> usuwa, gdy nie -> wyjątek")
    void shouldDeleteOrThrowWhenNotFound() {
        // given
        long existingId = 10L;
        long nonExistingId = 999L;

        given(postRepository.existsById(existingId)).willReturn(true);
        given(postRepository.existsById(nonExistingId)).willReturn(false);

        // when (happy path)
        postService.deletePost(existingId);

        // then
        verify(postRepository).deleteById(existingId);

        // when/then (exception path)
        assertThrows(ElementNotFoundException.class, () -> postService.deletePost(nonExistingId));
        verify(postRepository, never()).deleteById(nonExistingId);
    }

    @Test
    @DisplayName("getPost() - istniejący ID -> zwraca post, nieistniejący -> wyjątek")
    void shouldReturnPostOrThrow() {
        // given
        Post post = new Post();
        post.setId(11L);

        given(postRepository.findById(11L)).willReturn(Optional.of(post));
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Post found = postService.getPost(11);

        // then
        assertNotNull(found);
        assertEquals(11L, found.getId());

        // when/then
        assertThrows(ElementNotFoundException.class, () -> postService.getPost(999));
    }

    @Test
    @DisplayName("savePostPDF(PostCreateDTO) - rzuca FileStorageException przy błędzie IO")
    void shouldThrowFileStorageExceptionWhenIOFails() throws IOException {
        // given
        MultipartFile mockFile = mock(MultipartFile.class);
        given(mockFile.getOriginalFilename()).willReturn("test.pdf");
        given(mockFile.getInputStream()).willThrow(new IOException("Test I/O error"));

        PostCreateDTO dto = new PostCreateDTO();
        dto.setPdfFile(mockFile);

        // when & then
        assertThrows(FileStorageException.class, () -> postService.savePostPDF(dto));
    }

    @Test
    @DisplayName("searchPosts() - gdy keyword puste/null, zwraca findAll()")
    void shouldReturnFindAllWhenKeywordNullOrEmpty() {
        // given
        Page<Post> mockPage = new PageImpl<>(Collections.emptyList());
        given(postRepository.findAll(any(Pageable.class))).willReturn(mockPage);

        // when
        Page<Post> result1 = postService.searchPosts(null, 0, 10, PostService.PostSortType.DEFAULT);
        Page<Post> result2 = postService.searchPosts("    ", 0, 10, PostService.PostSortType.DEFAULT);

        // then
        assertSame(mockPage, result1);
        assertSame(mockPage, result2);
        verify(postRepository, times(2)).findAll(any(Pageable.class));
        verify(postRepository, never()).findByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("searchPosts() - gdy keyword niepuste, wywołuje findByTitleContainingIgnoreCase()")
    void shouldSearchByKeyword() {
        // given
        Page<Post> mockPage = new PageImpl<>(Collections.emptyList());
        given(postRepository.findByTitleContainingIgnoreCase(eq("crochet"), any(Pageable.class))).willReturn(mockPage);

        // when
        Page<Post> result = postService.searchPosts("crochet", 0, 10, PostService.PostSortType.DEFAULT);

        // then
        assertSame(mockPage, result);
        verify(postRepository).findByTitleContainingIgnoreCase(eq("crochet"), any(Pageable.class));
        verify(postRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("mapSortParamToEnum() - sprawdzanie różnych parametrów")
    void shouldMapSortParam() {
        // given
        // when
        PostService.PostSortType s1 = postService.mapSortParamToEnum("titleAsc");
        PostService.PostSortType s2 = postService.mapSortParamToEnum("dateNewest");
        PostService.PostSortType s3 = postService.mapSortParamToEnum("dateOldest");
        PostService.PostSortType s4 = postService.mapSortParamToEnum("likes");
        PostService.PostSortType s5 = postService.mapSortParamToEnum("cosInnego");

        // then
        assertEquals(PostService.PostSortType.TITLE_ASC, s1);
        assertEquals(PostService.PostSortType.DATE_NEWEST, s2);
        assertEquals(PostService.PostSortType.DATE_OLDEST, s3);
        assertEquals(PostService.PostSortType.LIKES, s4);
        assertEquals(PostService.PostSortType.DEFAULT, s5);
    }

    @Test
    @DisplayName("updateExistingPost() - gdy post istnieje, uaktualnia go")
    void shouldUpdateExistingPost() {
        // given
        Post existing = new Post();
        existing.setId(100L);
        existing.setTitle("Old Title");
        existing.setDescription("Old Desc");

        Tag t1 = new Tag();
        t1.setId(1L);

        Tag t2 = new Tag();
        t2.setId(2L);

        given(postRepository.findById(100L)).willReturn(Optional.of(existing));
        given(tagService.findById(1L)).willReturn(t1);
        given(tagService.findById(2L)).willReturn(t2);

        PostEditDTO editDTO = new PostEditDTO();
        editDTO.setId(100L);
        editDTO.setTitle("New Title");
        editDTO.setDescription("New Desc");
        editDTO.setTagIds(Set.of(1L, 2L));
        editDTO.setPdfFile(null); // brak nowego pliku

        // when
        postService.updateExistingPost(editDTO);

        // then
        verify(postRepository).save(existing);
        assertEquals("New Title", existing.getTitle());
        assertEquals("New Desc", existing.getDescription());
        assertEquals(2, existing.getTags().size());
    }

    @Test
    @DisplayName("updateExistingPost() - gdy post nie istnieje -> wyjątek")
    void shouldThrowWhenUpdatingNonExistingPost() {
        // given
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        PostEditDTO dto = new PostEditDTO();
        dto.setId(999L);

        // when & then
        assertThrows(ElementNotFoundException.class, () -> postService.updateExistingPost(dto));
        verify(postRepository, never()).save(any(Post.class));
    }
}
