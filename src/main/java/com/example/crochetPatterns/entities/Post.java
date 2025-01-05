package com.example.crochetPatterns.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.URL;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title" , nullable = false)
    @NotEmpty(message = "{post.titleEmpty}")
    @Size(max = 100 , message = "{post.titleTooLong}")
    private String title;

    @Column(name = "description" , nullable = true , columnDefinition = "TEXT")
    @Size(max = 10000 , message = "{post.descriptionTooLong}")
    private String description;

    @Column(name = "content_pdf" , nullable = true)
    @NotEmpty(message = "{post.emptyURL}")
    private String pdfFile;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false, nullable = false)
    @PastOrPresent(message = "{post.dateIsFuture}")
    private Timestamp creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @NotNull
    private User author;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "post_tags",
            joinColumns = { @JoinColumn(name = "post_id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
}

