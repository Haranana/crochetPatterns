package com.example.crochetPatterns.dtos;

import com.example.crochetPatterns.entities.PostTag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {

    private long id;

    private String name;

    private List<PostTag> post_tags = new ArrayList<>();
}
