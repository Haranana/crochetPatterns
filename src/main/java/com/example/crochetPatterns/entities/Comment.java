package com.example.crochetPatterns.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;

@Entity
@Table(name = "comments")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Positive(message = "{number.notPositive}")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @NotNull
    private User author;

    @Column(name = "text", nullable = false)
    @NotEmpty(message = "{comment.empty}")
    @Size(max = 1000, message = "{comment.tooLong}")
    private String text;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false, nullable = false)
    @PastOrPresent(message = "{comment.dateIsFuture}")
    private Timestamp creationDate;
}
