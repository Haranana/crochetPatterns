package com.example.crochetPatterns.dtos;


import com.example.crochetPatterns.Validation.FieldMatch;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldMatch(first = "password", second = "confirmPassword", message = "Hasło i potwierdzenie hasła muszą być takie same")
public class UserRegistrationDTO {

    @NotEmpty(message = "{user.usernameEmpty}")
    @Size(max = 50, message = "{user.usernameTooLong}")
    private String username;

    @Column(name = "email", nullable = false, length = 100)
    @Email(message = "{user.emailIncorrect}")
    private String email;

    @NotEmpty(message = "{user.passwordHashEmpty}")
    private String password;

    @NotEmpty(message = "{user.confirmPasswordEmpty}")
    private String confirmPassword;
}