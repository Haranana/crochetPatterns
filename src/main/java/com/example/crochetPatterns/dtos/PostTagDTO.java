package com.example.crochetPatterns.dtos;

import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostTagDTO {

    private long id;

    private Post post;

    private Tag tag;
}
