package com.example.crochetPatterns.dtos;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @NotNull
    @Positive(message = "{number.positive}")
    private long id;

    @NotEmpty(message = "{user.usernameEmpty}")
    @Size(max = 50 , message = "{user.usernameTooLong}")
    private String username;

    @Column(name = "email" , nullable = false , length = 100)
    @Email(message = "{user.emailIncorrect}")
    private String email;

    @NotEmpty(message =  "{user.passwordHashEmpty}")
    private String password;


    private String avatar;

    @Size(max = 4000 , message = "{user.bioTooLong}")
    private String bio;

    @PastOrPresent(message = "{user.dateIsFuture}")
    private Timestamp creationDate;

    private List<Long> postIds = new ArrayList<>();
    private List<Long> commentIds = new ArrayList<>();

}
