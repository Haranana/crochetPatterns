package com.example.crochetPatterns.dtos;

import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private long id;

    private Post post;

    private User author;

    private String title;

    private Timestamp creationDate;
}
