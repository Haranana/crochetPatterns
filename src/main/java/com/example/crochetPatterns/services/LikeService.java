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

    public void likePost(Long userId, Long postId) {

        if (!postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            PostLike postLike = new PostLike(userId, postId);
            postLikeRepository.save(postLike);
        }
    }

    public void unlikePost(Long userId, Long postId) {

        PostLikeId id = new PostLikeId(userId, postId);
        if (postLikeRepository.existsById(id)) {
            postLikeRepository.deleteById(id);
        }
    }

    public boolean hasLiked(Long userId, Long postId) {

        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    public long countLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }
}