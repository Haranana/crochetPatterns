package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.*;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.repositories.PostRepository;
import com.example.crochetPatterns.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentConverter {


    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentConverter(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Comment createComment(CommentReturnDTO commentReturnDTO){
        Comment comment = new Comment();
        comment.setAuthor( userRepository.findById(commentReturnDTO.getAuthorId()).orElse(null) );
        comment.setPost( (postRepository.findById(commentReturnDTO.getPostId())).get() );
        comment.setText(commentReturnDTO.getText());
        return comment;
    }

    public Comment createComment(CommentCreateDTO dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        // post
        Post post = postRepository.findById(dto.getPostId()).orElseThrow(() -> new RuntimeException("Post not found"));
        comment.setPost(post);

        // user
        User author = userRepository.findById(dto.getAuthorId()).orElse(null);
        comment.setAuthor(author); // może być null, jeśli tak byś chciał

        return comment;
    }


    public CommentReturnDTO createDTO(Comment comment) {
        CommentReturnDTO dto = new CommentReturnDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreationDate(comment.getCreationDate());
        dto.setPostId(comment.getPost().getId());

        // Jeśli author w encji jest null (konto usunięte), to dto.authorId będzie null
        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
        } else {
            dto.setAuthorId(null);
        }

        // Możemy też zaktualizować showableDate
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
