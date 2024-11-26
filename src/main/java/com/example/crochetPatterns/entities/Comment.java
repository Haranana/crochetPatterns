package com.example.crochetPatterns.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "comments")
@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = {"post" , "author"})
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "text" , nullable = false)
    private String title;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false, nullable = false)
    private Timestamp creationDate;

}
