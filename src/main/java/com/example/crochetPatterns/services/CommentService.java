package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.*;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.exceptions.ElementNotFoundException;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.repositories.CommentRepository;
import com.example.crochetPatterns.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CommentService {

    public enum CommentSortType{
        NEWEST,
        OLDEST,
        DEFAULT
    }

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository){

        this.commentRepository = commentRepository;
    }

    public void addNewComment(Comment comment){
        commentRepository.save(comment);
    }

    public List<Comment> findCommentsByIds(List<Long> ids) {

        return commentRepository.findAllById(ids);
    }

    public Page<Comment> getCommentDTOPageByPost(int pageId, int pageSize, CommentSortType commentSortType, int postId) {

        Sort sort = createSortObject(commentSortType);
        Pageable pageable = PageRequest.of(pageId, pageSize, sort);
        return commentRepository.findByPostId(Integer.toUnsignedLong(postId), pageable);
    }

    public Page<Comment> getCommentDTOPageByUser(int pageId, int pageSize, CommentSortType commentSortType, int userId){

        Sort sort = createSortObject(commentSortType);
        Pageable pageable = PageRequest.of(pageId, pageSize, sort);
        return commentRepository.findByAuthorId(Integer.toUnsignedLong(userId), pageable);
    }

    public Comment getCommentDTO(int id){
        return commentRepository.findById(Integer.toUnsignedLong(id)).orElseThrow(() -> new ElementNotFoundException("Comment not found: " + id));
    }

    public void updateExistingComment(CommentEditDTO commentEditDTO){

        Comment existingComment = commentRepository.findById(commentEditDTO.getId())
                .orElseThrow(() -> new ElementNotFoundException("Comment not found: " + commentEditDTO.getId()));
        existingComment.setText(commentEditDTO.getText());
        commentRepository.save(existingComment);
    }

    private Sort createSortObject(CommentSortType commentSortType) {

        Sort returnSort = Sort.by("id").ascending();
        switch (commentSortType){
            case NEWEST -> returnSort = Sort.by("creationDate").descending();
            case OLDEST -> returnSort = Sort.by("creationDate").ascending();
            case DEFAULT -> returnSort = Sort.by("id").ascending();
        }
        return returnSort;
    }
}
