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
@Table(name = "posts")
@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = {"tags", "comments" , "author" , "additionalInfo"})
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title" , nullable = false)
    private String title;

    @Column(name = "description" , nullable = true , columnDefinition = "TEXT")
    private String description;

    @Column(name = "content_pdf" , nullable = true)
    private String pdfFile;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false, nullable = false)
    private Timestamp creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "post_tags", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "comments", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "additional_info", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AdditionalInfo additionalInfo;

    
}
