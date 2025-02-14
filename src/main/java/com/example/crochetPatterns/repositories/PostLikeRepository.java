package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.PostLike;
import com.example.crochetPatterns.entities.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.id.postId = :postId")
    long countByPostId(@Param("postId") Long postId);

    @Query("SELECT CASE WHEN COUNT(pl) > 0 THEN TRUE ELSE FALSE END " +
            "FROM PostLike pl " +
            "WHERE pl.id.userId = :userId AND pl.id.postId = :postId")
    boolean existsByUserIdAndPostId(Long userId, Long postId);

}