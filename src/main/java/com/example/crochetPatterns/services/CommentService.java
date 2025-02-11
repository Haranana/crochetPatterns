package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.*;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.exceptions.ElementNotFoundException;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.repositories.CommentRepository;
import com.example.crochetPatterns.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    public enum CommentSortType{
        NEWEST,
        OLDEST,
        DEFAULT
    }

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentConverter commentConverter;

    public CommentService(CommentRepository commentRepository, CommentConverter commentConverter, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.commentConverter = commentConverter;
        this.userRepository = userRepository;
    }

    public void addNewComment(CommentCreateDTO commentDTO){
        Comment comment = commentConverter.createComment(commentDTO);
        commentRepository.save(comment);
    }

    public void addNewComment(CommentReturnDTO commentReturnDTO){
        Comment comment = commentConverter.createComment(commentReturnDTO);
        commentRepository.save(comment);
    }

    public void removeComment(int commentId){
        commentRepository.deleteById(Integer.toUnsignedLong(commentId));
    }

    public Page<Comment> getCommentDTOPageByPost(int pageId, int pageSize, CommentSortType commentSortType, int postId){
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
        return commentRepository.findById(Integer.toUnsignedLong(id))
                .orElseThrow(() -> new ElementNotFoundException("Comment not found: " + id));
    }

    public List<Integer> createPageNumbers(int page, int totalPages) {
        int numbers = 3;
        List<Integer> pageNumbers = new ArrayList<>();
        if(totalPages <= numbers * 2) {
            for(int i = 0; i < totalPages; ++i) {
                pageNumbers.add(i);
            }
        } else {
            List<Integer> firstPages = new ArrayList<>();
            List<Integer> middlePages = new ArrayList<>();
            List<Integer> lastPages = new ArrayList<>();
            firstPages.add(0);
            firstPages.add(1);
            firstPages.add(2);
            lastPages.add(totalPages - 3);
            lastPages.add(totalPages - 2);
            lastPages.add(totalPages - 1);

            if((page != totalPages - 1) && (page != 0)){
                if(page - 1 > numbers) {
                    middlePages.add(-1);
                }
                middlePages.add(page - 1);
                middlePages.add(page);
                if(page + 1 < totalPages){
                    middlePages.add(page + 1);
                }
                if(page + 1 < totalPages - 3 - 1) {
                    middlePages.add(-1);
                }
            } else {
                middlePages.add(-1);
            }
            pageNumbers.addAll(firstPages);
            for(Integer i : middlePages) {
                if(i == -1) {
                    pageNumbers.add(i);
                } else if(!pageNumbers.contains(i)){
                    pageNumbers.add(i);
                }
            }
            for(Integer i : lastPages) {
                if(i == -1) {
                    pageNumbers.add(i);
                } else if(!pageNumbers.contains(i)){
                    pageNumbers.add(i);
                }
            }
        }
        return pageNumbers;
    }

    public void updateExistingComment(CommentEditDTO commentEditDTO){
        Comment existingComment = commentRepository.findById(commentEditDTO.getId())
                .orElseThrow(() -> new ElementNotFoundException("Comment not found: " + commentEditDTO.getId()));
        existingComment.setText(commentEditDTO.getText());
        commentRepository.save(existingComment);
    }

    public void updateDTOShowableDate(CommentReturnDTO commentReturnDTO){
        commentReturnDTO.setShowableDate(commentReturnDTO.getCreationDate().toString().substring(0, commentReturnDTO.getCreationDate().toString().lastIndexOf(':')));

        Instant instant1 = commentReturnDTO.getCreationDate().toInstant();
        Instant instant2 = Instant.now();
        Duration duration = Duration.between(instant1, instant2);

        if(duration.toDays() >= 365){
            commentReturnDTO.setCreationTimeValueType(CommentReturnDTO.CreationTimeValueType.YEAR);
            commentReturnDTO.setCreationTimeValue((int) (duration.toDays() / 365));
        }
        else if(duration.toDays() >= 30){
            commentReturnDTO.setCreationTimeValueType(CommentReturnDTO.CreationTimeValueType.MONTH);
            commentReturnDTO.setCreationTimeValue((int) (duration.toDays() / 30));
        } else if (duration.toDays() >= 7) {
            commentReturnDTO.setCreationTimeValueType(CommentReturnDTO.CreationTimeValueType.WEEK);
            commentReturnDTO.setCreationTimeValue((int) (duration.toDays() / 7));
        } else if (duration.toHours() >= 24) {
            commentReturnDTO.setCreationTimeValueType(CommentReturnDTO.CreationTimeValueType.DAY);
            commentReturnDTO.setCreationTimeValue((int) (duration.toHours() / 24));
        } else if(duration.toMinutes() >= 60){
            commentReturnDTO.setCreationTimeValueType(CommentReturnDTO.CreationTimeValueType.HOUR);
            commentReturnDTO.setCreationTimeValue((int) (duration.toMinutes() / 60));
        } else if(duration.toMinutes() >= 1){
            commentReturnDTO.setCreationTimeValueType(CommentReturnDTO.CreationTimeValueType.MINUTE);
            commentReturnDTO.setCreationTimeValue((int) (duration.toMinutes()));
        } else{
            commentReturnDTO.setCreationTimeValueType(CommentReturnDTO.CreationTimeValueType.SECOND);
            commentReturnDTO.setCreationTimeValue((int) (duration.toSeconds()));
        }
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
