package com.example.crochetPatterns.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Setter
@Getter
@ToString(exclude = {"posts", "comments"})
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "{number.positive}")
    private Long id;

    @Column(name = "username" , nullable = false , length = 30)
    @NotEmpty(message = "{user.usernameEmpty}")
    @Size(max = 50 , message = "{user.usernameTooLong}")
    private String username;

    @Column(name = "email" , nullable = false , length = 100)
    @Email(message = "{user.emailIncorrect}")
    private String email;

    @Column(name = "password_hash" , nullable = false)
    @NotEmpty(message =  "{user.passwordHashEmpty}")
    private String password;

    @Column(name = "avatar" , nullable = true)
    private String avatar;

    @Column(name = "bio" , nullable = true , columnDefinition = "TEXT")
    @Size(max = 4000 , message = "{user.bioTooLong}")
    private String bio;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false, nullable = false)
    @PastOrPresent(message = "{user.dateIsFuture}")
    private Timestamp creationDate;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

}
