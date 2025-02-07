package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.UserDTO;
import com.example.crochetPatterns.dtos.UserRegistrationDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.repositories.CommentRepository;
import com.example.crochetPatterns.repositories.PostRepository;
import jakarta.validation.constraints.Null;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;




@Component
public class UserConverter {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UserConverter(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public User createUser(UserRegistrationDTO userDTO , String encodedPassword){
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encodedPassword);
        //user.setAvatar(); Avatar is NULL by default
        user.setBio("");
        user.setPosts(new ArrayList<>());
        user.setComments(new ArrayList<>());
        return user;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setAvatar(userDTO.getAvatar());
        user.setBio(userDTO.getBio());
        user.setPosts(postRepository.findAllById(userDTO.getPostIds()));
        user.setComments(commentRepository.findAllById(userDTO.getCommentIds()));
        return user;
    }

    public UserDTO createDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setBio(user.getBio());
        userDTO.setPostIds(user.getPosts().stream().map(Post::getId).collect(Collectors.toList()));
        userDTO.setCommentIds(user.getComments().stream().map(Comment::getId).collect(Collectors.toList()));

        return userDTO;
    }
}
