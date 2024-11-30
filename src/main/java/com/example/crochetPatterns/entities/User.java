package com.example.crochetPatterns.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Long id;

    @Column(name = "username" , nullable = false , length = 30)
    private String username;

    @Column(name = "email" , nullable = false , length = 100)
    private String email;

    @Column(name = "password_hash" , nullable = false)
    private String password_hash;

    @Column(name = "avatar" , nullable = true)
    private String avatar;

    @Column(name = "bio" , nullable = true , columnDefinition = "TEXT")
    private String bio;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false, nullable = false)
    private Timestamp creationDate;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    // Konstruktorzy...
}
