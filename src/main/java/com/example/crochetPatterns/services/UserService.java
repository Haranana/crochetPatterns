package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.CommentDTO;
import com.example.crochetPatterns.dtos.UserDTO;
import com.example.crochetPatterns.entities.Comment;
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

    public void removeComment(int userId){
        userRepository.deleteById(Integer.toUnsignedLong(userId));
    }

    public User getUserDTO(int id){
        Optional<User> returnUser = userRepository.findById(Integer.toUnsignedLong(id));
        return returnUser.orElse(null);
    }
}
