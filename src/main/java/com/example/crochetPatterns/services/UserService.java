package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

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

    public void updateUserProfile(UserEditDTO userEditDTO) {
        User user = userRepository.findById(userEditDTO.getId())
                .orElseThrow(() -> new RuntimeException("User not found: " + userEditDTO.getId()));

        user.setUsername(userEditDTO.getUsername());
        user.setBio(userEditDTO.getBio());
        MultipartFile avatarFile = userEditDTO.getAvatarFile();

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String newAvatarPath = saveAvatarFile(avatarFile);
            user.setAvatar(newAvatarPath);
            /*
            if (!newAvatarPath.isEmpty()) {

            }
            */

        }
        userRepository.save(user);
    }

    private String saveAvatarFile(MultipartFile avatarFile) {
        try {
            String originalFilename = avatarFile.getOriginalFilename();
            String uniqueName = System.currentTimeMillis() + "_" + originalFilename;
            // Zakładamy, że nowe avatary zapisywane są w folderze: src/main/resources/static/images/
            Path uploadDir = Paths.get("src/main/resources/static/images");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(uniqueName);
            Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            // Zwracamy ścieżkę względną, która umożliwi wyświetlenie obrazu (np. "/images/unikalnaNazwa.png")
            return "/images/" + uniqueName;
        } catch (IOException e) {
            System.out.println("Błąd przy zapisie pliku avatara: " + e.getMessage());
            return "";
        }
    }

    public boolean changeUserPassword(UserPasswordChangeDTO dto, PasswordEncoder passwordEncoder) {
        Optional<User> optionalUser = userRepository.findById(dto.getId());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Użytkownik nie został znaleziony o id: " + dto.getId());
        }
        User user = optionalUser.get();
        // Sprawdzamy, czy podane aktualne hasło zgadza się z tym zapisanym (przy użyciu PasswordEncoder.matches)
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            return false;
        }
        // Ustawiamy nowe hasło – kodujemy je przy pomocy PasswordEncoder
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
