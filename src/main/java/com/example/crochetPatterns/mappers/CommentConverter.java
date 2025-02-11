package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.*;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.repositories.PostRepository;
import com.example.crochetPatterns.repositories.UserRepository;
import com.example.crochetPatterns.services.PostService;
import com.example.crochetPatterns.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentConverter {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public CommentConverter(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    public Comment createComment(CommentReturnDTO commentReturnDTO){
        Comment comment = new Comment();
        comment.setAuthor(userService.getUserOrNull(commentReturnDTO.getAuthorId()));
        comment.setPost(postService.getPost(Math.toIntExact(commentReturnDTO.getPostId())));
        comment.setText(commentReturnDTO.getText());
        return comment;
    }

    public Comment createComment(CommentCreateDTO dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setPost(postService.getPost(Math.toIntExact(dto.getPostId())));
        comment.setAuthor(userService.getUserOrNull(dto.getAuthorId()));
        return comment;
    }

    public CommentReturnDTO createDTO(Comment comment) {
        CommentReturnDTO dto = new CommentReturnDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreationDate(comment.getCreationDate());
        dto.setPostId(comment.getPost().getId());

        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
        } else {
            dto.setAuthorId(null);
        }

        dto.updateShowableDate();
        return dto;
    }

    public CommentEditDTO createEditDTOFromComment(Comment comment){
        CommentEditDTO dto = new CommentEditDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setText(comment.getText());
        return dto;
    }

    public List<CommentReturnDTO> createDTO(List<Comment> commentList) {
        List<CommentReturnDTO> result = new ArrayList<>();
        result = commentList.stream().map(this::createDTO).collect(Collectors.toList());
        for(CommentReturnDTO commentReturnDTO : result){
            commentReturnDTO.updateShowableDate();
        }
        return result;
    }


}
