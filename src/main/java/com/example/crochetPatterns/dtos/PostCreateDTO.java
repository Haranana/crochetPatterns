package com.example.crochetPatterns.dtos;


import com.example.crochetPatterns.Validation.pdfFileConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

/*
    represents PostDTO before saving PDF file
    as a result has attribute PDF file of type MultipartFile instead of a path to it
    It also doesn't have any auto generated attributes nor comments
    In future this class will probably be also used to modify posts
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateDTO {

    @NotEmpty(message = "{post.titleEmpty}")
    @Size(max = 100 , message = "{post.titleTooLong}")
    private String title;

    @Size(max = 10000 , message = "{post.descriptionTooLong}")
    private String description;

    @pdfFileConstraint(message = "{post.wrongFile}")
    private MultipartFile pdfFile;
    
    @NotNull
    @Positive(message = "{number.positive}")
    private Long authorId;

    private Set<Long> tagIds = new HashSet<>();

}
