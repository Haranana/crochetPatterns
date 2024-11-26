package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
