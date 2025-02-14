package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByAuthorId(Long authorId, Pageable pageable);
    Page<Comment> findByAuthor(User author, Pageable pageable);
    Page<Comment> findByPostId(Long postId, Pageable pageable);
}
