package com.example.crochetPatterns.dtos;

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
public class UserRegistrationDTO {

    @NotEmpty(message = "{user.usernameEmpty}")
    @Size(max = 50 , message = "{user.usernameTooLong}")
    private String username;

    @Column(name = "email" , nullable = false , length = 100)
    @Email(message = "{user.emailIncorrect}")
    private String email;

    @NotEmpty(message =  "{user.passwordHashEmpty}")
    private String password;
}
