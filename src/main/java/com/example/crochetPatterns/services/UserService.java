package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.*;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.exceptions.ElementNotFoundException;
import com.example.crochetPatterns.exceptions.FileStorageException;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
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

    public User addNewUser(UserRegistrationDTO userDTO, String encodedPassword){
        User user = userConverter.createUser(userDTO, encodedPassword);
        userRepository.save(user);
        return user;
    }

    public User getUserByUsername(String username){
        User user = getUserDTO(userRepository.findUserIdByUsername(username));
        if (user == null) {
            throw new ElementNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    public User getUserDTO(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("User not found: " + id));
    }

    public User getUserDTO(int id){
        return userRepository.findById(Integer.toUnsignedLong(id))
                .orElseThrow(() -> new ElementNotFoundException("User not found: " + id));
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
                .orElseThrow(() -> new ElementNotFoundException("User not found: " + userEditDTO.getId()));
        user.setUsername(userEditDTO.getUsername());
        user.setBio(userEditDTO.getBio());

        if(userEditDTO.getAvatarFile() != null) {
            MultipartFile avatarFile = userEditDTO.getAvatarFile();

            if (avatarFile != null && !avatarFile.isEmpty()) {
                String newAvatarPath = saveAvatarFile(avatarFile);
                user.setAvatar(newAvatarPath);
            }
        }
        userRepository.save(user);
    }

    private String saveAvatarFile(MultipartFile avatarFile) {
        try {
            String originalFilename = avatarFile.getOriginalFilename();
            String uniqueName = System.currentTimeMillis() + "_" + originalFilename;
            Path uploadDir = Paths.get("src/main/resources/static/images");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(uniqueName);
            Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/images/" + uniqueName;
        } catch (IOException e) {
            throw new FileStorageException("Błąd przy zapisie pliku avatara", e);
        }
    }

    public boolean changeUserPassword(UserPasswordChangeDTO dto, PasswordEncoder passwordEncoder) {
        Optional<User> optionalUser = userRepository.findById(dto.getId());
        if (optionalUser.isEmpty()) {
            throw new ElementNotFoundException("Użytkownik nie został znaleziony o id: " + dto.getId());
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}