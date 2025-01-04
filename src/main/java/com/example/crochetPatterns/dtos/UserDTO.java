package com.example.crochetPatterns.dtos;

import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private long id;
    private String username;
    private String email;
    private String password_hash;
    private String avatar;
    private String bio;
    private Timestamp creationDate;

    private List<Long> postIds = new ArrayList<>();
    private List<Long> commentIds = new ArrayList<>();

}
