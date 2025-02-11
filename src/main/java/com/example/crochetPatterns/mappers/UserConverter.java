package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.UserReturnDTO;
import com.example.crochetPatterns.dtos.UserEditDTO;
import com.example.crochetPatterns.dtos.UserRegistrationDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.repositories.CommentRepository;
import com.example.crochetPatterns.repositories.PostRepository;
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

    // Ustaw domyślny avatar dla nowych użytkowników
    public User createUser(UserRegistrationDTO userDTO, String encodedPassword) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encodedPassword);
        // Ustawiamy domyślny avatar (ścieżka względna – upewnij się, że plik istnieje)
        user.setAvatar("/images/defaultavatar.png");
        user.setEnabled(false); // Nowe konta domyślnie nie są aktywne
        user.setBio("");
        user.setPosts(new ArrayList<>());
        user.setComments(new ArrayList<>());
        return user;
    }

    public User createUser(UserReturnDTO userReturnDTO) {
        User user = new User();
        user.setUsername(userReturnDTO.getUsername());
        user.setEmail(userReturnDTO.getEmail());
        user.setPassword(userReturnDTO.getPassword());
        user.setAvatar(userReturnDTO.getAvatar());
        user.setBio(userReturnDTO.getBio());
        user.setEnabled(userReturnDTO.isEnabled());
        user.setPosts(postRepository.findAllById(userReturnDTO.getPostIds()));
        user.setComments(commentRepository.findAllById(userReturnDTO.getCommentIds()));
        return user;
    }

    public UserReturnDTO createDTO(User user) {
        UserReturnDTO userReturnDTO = new UserReturnDTO();
        userReturnDTO.setId(user.getId());
        userReturnDTO.setUsername(user.getUsername());
        userReturnDTO.setEmail(user.getEmail());
        userReturnDTO.setPassword(user.getPassword());
        userReturnDTO.setAvatar(user.getAvatar());
        userReturnDTO.setBio(user.getBio());
        userReturnDTO.setEnabled(user.isEnabled());
        userReturnDTO.setPostIds(user.getPosts().stream().map(Post::getId).collect(Collectors.toList()));
        userReturnDTO.setCommentIds(user.getComments().stream().map(Comment::getId).collect(Collectors.toList()));

        return userReturnDTO;
    }

    public UserEditDTO createEditDTO(User user) {
        UserEditDTO dto = new UserEditDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setBio(user.getBio());
        // Nie ustawiamy avatarFile – to pole w formularzu będzie puste, dopiero jeśli użytkownik przesła nowy plik
        return dto;
    }
}
