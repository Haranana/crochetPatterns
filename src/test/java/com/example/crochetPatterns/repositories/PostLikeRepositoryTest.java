package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.PostLike;
import com.example.crochetPatterns.entities.PostLikeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostLikeRepositoryTest {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @BeforeEach
    void setUp() {
        // given - dodajemy kilka polubień do bazy
        PostLike pl1 = new PostLike(1L, 10L);
        PostLike pl2 = new PostLike(2L, 10L);
        PostLike pl3 = new PostLike(3L, 20L);

        postLikeRepository.save(pl1);
        postLikeRepository.save(pl2);
        postLikeRepository.save(pl3);
    }

    @Test
    @DisplayName("countByPostId() - powinno zwrócić liczbę polubień dla konkretnego postId")
    void shouldCountLikesByPostId() {
        // when
        long count10 = postLikeRepository.countByPostId(10L);
        long count20 = postLikeRepository.countByPostId(20L);
        long count999 = postLikeRepository.countByPostId(999L);

        // then
        assertEquals(2, count10);   // userId=1 i userId=2 lubią postId=10
        assertEquals(1, count20);   // userId=3 lubi postId=20
        assertEquals(0, count999);
    }

    @Test
    @DisplayName("existsByUserIdAndPostId() - sprawdza czy polubienie istnieje")
    void shouldCheckIfLikeExists() {
        // when
        boolean ex1 = postLikeRepository.existsByUserIdAndPostId(1L, 10L);
        boolean ex2 = postLikeRepository.existsByUserIdAndPostId(2L, 10L);
        boolean ex3 = postLikeRepository.existsByUserIdAndPostId(99L, 10L);

        // then
        assertTrue(ex1);
        assertTrue(ex2);
        assertFalse(ex3);
    }
}
