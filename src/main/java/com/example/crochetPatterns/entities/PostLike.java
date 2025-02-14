package com.example.crochetPatterns.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLike {

    @EmbeddedId
    private PostLikeId id;

    public PostLike(Long userId, Long postId) {
        this.id = new PostLikeId(userId, postId);
    }
}