package com.example.crochetPatterns.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TagReturnDTO {

    @Positive(message = "{number.positive}")
    private long id;

    @NotEmpty(message = "{tag.nameEmpty}")
    @Size(max = 100 , message = "{tag.nameTooLong}")
    private String name;

    private List<Long> postIds = new ArrayList<>();
}
