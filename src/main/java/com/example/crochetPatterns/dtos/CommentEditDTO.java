package com.example.crochetPatterns.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentEditDTO {
    private Long id;

    @NotEmpty(message = "{comment.empty}")
    @Size(max = 1000, message = "{comment.tooLong}")
    private String text;
}
