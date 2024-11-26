package com.example.crochetPatterns.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_tags")
@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = {"post" , "tag"})
@AllArgsConstructor
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
