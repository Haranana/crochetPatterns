package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);
}
