package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String name);
}
