package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Post, Long> {
}
