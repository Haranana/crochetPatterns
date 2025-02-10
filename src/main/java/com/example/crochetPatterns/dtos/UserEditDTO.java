package com.example.crochetPatterns.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserEditDTO {

    @NotNull(message = "Brak identyfikatora użytkownika")
    private Long id;

    @NotEmpty(message = "{user.usernameEmpty}")
    @Size(max = 50, message = "{user.usernameTooLong}")
    private String username;

    @Size(max = 4000, message = "{user.bioTooLong}")
    private String bio;

    // Opcjonalnie – nowe zdjęcie profilowe przesłane przez użytkownika:
    private MultipartFile avatarFile;
}