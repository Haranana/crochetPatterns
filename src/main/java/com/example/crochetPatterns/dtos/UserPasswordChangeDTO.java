package com.example.crochetPatterns.dtos;

import com.example.crochetPatterns.Validation.FieldMatch;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "Nowe hasła muszą być takie same")
public class UserPasswordChangeDTO {

    // Identyfikator użytkownika – zakładamy, że pobieramy go z kontekstu zalogowanego użytkownika.
    private Long id;

    @NotEmpty(message = "Pole z aktualnym hasłem nie może być puste")
    private String currentPassword;

    @NotEmpty(message = "Nowe hasło nie może być puste")
    @Size(min = 6, message = "Nowe hasło musi mieć co najmniej 6 znaków")
    private String newPassword;

    @NotEmpty(message = "Potwierdzenie hasła nie może być puste")
    private String confirmNewPassword;
}