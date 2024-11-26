package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
