package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.*;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.repositories.CommentRepository;
import com.example.crochetPatterns.repositories.PostRepository;
import com.example.crochetPatterns.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentConverter {


    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentConverter(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Comment createComment(CommentDTO commentDTO){
        Comment comment = new Comment();
        comment.setAuthor( (userRepository.findById(commentDTO.getAuthorId())).get() );
        comment.setPost( (postRepository.findById(commentDTO.getPostId())).get() );
        comment.setText(commentDTO.getText());
        return comment;
    }

    public Comment createComment(CommentFormDTO commentDTO){
        Comment comment = new Comment();
        comment.setAuthor( (userRepository.findById(commentDTO.getAuthorId())).get() );
        comment.setPost( (postRepository.findById(commentDTO.getPostId())).get() );
        comment.setText(commentDTO.getText());
        return comment;
    }


    public CommentDTO createDTO(Comment comment){
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setPostId(comment.getPost() != null ? comment.getPost().getId() : null);
        commentDTO.setAuthorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null);
        commentDTO.setText(comment.getText());
        commentDTO.setCreationDate(comment.getCreationDate());

        return commentDTO;
    }

    public CommentEditDTO createEditDTOFromComment(Comment comment){
        CommentEditDTO dto = new CommentEditDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setText(comment.getText());
        return dto;
    }

    public List<CommentDTO> createDTO(List<Comment> comments){
        List<CommentDTO> commentDTOs = new ArrayList<>();
        for(Comment comment : comments) {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(comment.getId());
            commentDTO.setPostId(comment.getPost() != null ? comment.getPost().getId() : null);
            commentDTO.setAuthorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null);
            commentDTO.setText(comment.getText());
            commentDTO.setCreationDate(comment.getCreationDate());

            commentDTOs.add(commentDTO);
        }
        return commentDTOs;
    }


}
