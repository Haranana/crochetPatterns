package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
