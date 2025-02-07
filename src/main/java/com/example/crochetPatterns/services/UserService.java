package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.CommentDTO;
import com.example.crochetPatterns.dtos.PostFormDTO;
import com.example.crochetPatterns.dtos.UserDTO;
import com.example.crochetPatterns.dtos.UserRegistrationDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.repositories.CommentRepository;
import com.example.crochetPatterns.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final UserConverter userConverter;

    public UserService(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    public void addNewUser(UserDTO userDTO){
        User user = userConverter.createUser(userDTO);
        userRepository.save(user);
    }

    public User addNewUser(UserRegistrationDTO userDTO , String encodedPassword){
        User user = userConverter.createUser(userDTO , encodedPassword);
        userRepository.save(user);
        return user;
    }

    public User getUserByUsername(String username){
        return getUserDTO(userRepository.findUserIdByUsername(username));
    }

    public User getUserDTO(Long id){
        Optional<User> returnUser = userRepository.findById(id);
        return returnUser.orElse(null);
    }

    public User getUserDTO(int id){
        Optional<User> returnUser = userRepository.findById(Integer.toUnsignedLong(id));
        return returnUser.orElse(null);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
