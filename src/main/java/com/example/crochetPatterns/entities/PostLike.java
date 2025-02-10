package com.example.crochetPatterns.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Encja łącząca User i Post w relacji Many-to-Many (zawiera jedynie user_id i post_id).
 * Klucz główny to kombinacja user_id + post_id.
 */
@Entity
@Table(name = "post_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLike {

    @EmbeddedId
    private PostLikeId id;

    // Konstruktor pomocniczy (gdy chcemy utworzyć obiekt bezpośrednio z userId i postId)
    public PostLike(Long userId, Long postId) {
        this.id = new PostLikeId(userId, postId);
    }
}