package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Long findUserIdByUsername(@Param("username") String username);

    // Sprawdzenie istnienia po username:
    boolean existsByUsername(String username);

    // Sprawdzenie istnienia po emailu:
    boolean existsByEmail(String email);

    // Pobranie użytkownika po username (do logowania Spring Security):
    Optional<User> findByUsername(String username);
}
