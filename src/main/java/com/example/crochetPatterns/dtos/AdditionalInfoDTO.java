package com.example.crochetPatterns.dtos;

import com.example.crochetPatterns.entities.Post;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalInfoDTO {
    private Post post;
}
