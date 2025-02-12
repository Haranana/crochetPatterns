package com.example.crochetPatterns.services;

import com.example.crochetPatterns.entities.PostLike;
import com.example.crochetPatterns.entities.PostLikeId;
import com.example.crochetPatterns.repositories.PostLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        // ...
    }

    @Test
    @DisplayName("likePost() - gdy nie polubione -> tworzy nowy PostLike")
    void shouldCreateLikeIfNotExists() {
        // given
        Long userId = 10L;
        Long postId = 20L;
        given(postLikeRepository.existsByUserIdAndPostId(userId, postId)).willReturn(false);

        // when
        likeService.likePost(userId, postId);

        // then
        verify(postLikeRepository).save(any(PostLike.class));
    }

    @Test
    @DisplayName("likePost() - gdy już polubione -> nic nie robi")
    void shouldNotCreateLikeIfAlreadyExists() {
        // given
        Long userId = 10L;
        Long postId = 20L;
        given(postLikeRepository.existsByUserIdAndPostId(userId, postId)).willReturn(true);

        // when
        likeService.likePost(userId, postId);

        // then
        verify(postLikeRepository, never()).save(any(PostLike.class));
    }

    @Test
    @DisplayName("unlikePost() - gdy istnieje -> usuwa z repo")
    void shouldUnlikeIfExists() {
        // given
        Long userId = 1L;
        Long postId = 2L;
        PostLikeId compositeId = new PostLikeId(userId, postId);

        given(postLikeRepository.existsById(compositeId)).willReturn(true);

        // when
        likeService.unlikePost(userId, postId);

        // then
        verify(postLikeRepository).deleteById(compositeId);
    }

    @Test
    @DisplayName("unlikePost() - gdy nie istnieje -> nic nie robi")
    void shouldDoNothingIfNotLiked() {
        // given
        Long userId = 1L;
        Long postId = 2L;
        PostLikeId compositeId = new PostLikeId(userId, postId);

        given(postLikeRepository.existsById(compositeId)).willReturn(false);

        // when
        likeService.unlikePost(userId, postId);

        // then
        verify(postLikeRepository, never()).deleteById(any(PostLikeId.class));
    }

    @Test
    @DisplayName("hasLiked() - sprawdza istnienie polubienia")
    void shouldCheckIfUserHasLiked() {
        // given
        given(postLikeRepository.existsByUserIdAndPostId(5L, 6L)).willReturn(true);

        // when
        boolean result = likeService.hasLiked(5L, 6L);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("countLikes() - liczy polubienia dla postu")
    void shouldCountLikes() {
        // given
        given(postLikeRepository.countByPostId(100L)).willReturn(42L);

        // when
        long count = likeService.countLikes(100L);

        // then
        assertEquals(42L, count);
    }
}
