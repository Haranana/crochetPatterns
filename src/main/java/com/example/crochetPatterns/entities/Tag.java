package com.example.crochetPatterns.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = {"post_tags"})
@AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "name" , nullable = false)
    private String name;

    @OneToMany(mappedBy = "post_tags", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostTag> post_tags = new ArrayList<>();
}
