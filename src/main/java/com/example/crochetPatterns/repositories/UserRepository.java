package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Long findUserIdByUsername(@Param("username") String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
