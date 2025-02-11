package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.UserReturnDTO;
import com.example.crochetPatterns.dtos.UserEditDTO;
import com.example.crochetPatterns.dtos.UserRegistrationDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.services.PostService;
import com.example.crochetPatterns.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class UserConverter {

    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public UserConverter(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
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
        // Pobieramy posty oraz komentarze korzystając z serwisów
        user.setPosts(postService.findPostsByIds(userReturnDTO.getPostIds()));
        user.setComments(commentService.findCommentsByIds(userReturnDTO.getCommentIds()));
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
        // Pole avatarFile nie jest ustawiane – formularz pozostawi je puste, dopóki użytkownik nie prześle nowego pliku.
        return dto;
    }
}
