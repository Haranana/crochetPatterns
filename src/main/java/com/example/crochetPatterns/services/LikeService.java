package com.example.crochetPatterns.services;

import com.example.crochetPatterns.entities.PostLike;
import com.example.crochetPatterns.entities.PostLikeId;
import com.example.crochetPatterns.repositories.PostLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {

    private final PostLikeRepository postLikeRepository;

    public LikeService(PostLikeRepository postLikeRepository) {
        this.postLikeRepository = postLikeRepository;
    }

    /**
     * Polubienie postu przez danego użytkownika.
     * Jeśli użytkownik już polubił ten post, metoda nic nie robi (unikamy duplikatów).
     */
    public void likePost(Long userId, Long postId) {
        // Sprawdź, czy już istnieje polubienie
        if (!postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            PostLike postLike = new PostLike(userId, postId);
            postLikeRepository.save(postLike);
        }
    }

    /**
     * Cofnięcie polubienia (usunięcie rekordu).
     */
    public void unlikePost(Long userId, Long postId) {
        PostLikeId id = new PostLikeId(userId, postId);
        if (postLikeRepository.existsById(id)) {
            postLikeRepository.deleteById(id);
        }
    }

    /**
     * Sprawdzenie, czy dany użytkownik lubi post.
     */
    public boolean hasLiked(Long userId, Long postId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    /**
     * Zwraca liczbę polubień postu.
     */
    public long countLikes(Long postId) {
        System.out.println("Post id: " + postId);
        return postLikeRepository.countByPostId(postId);
    }
}