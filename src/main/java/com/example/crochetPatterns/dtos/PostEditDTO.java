package com.example.crochetPatterns.dtos;

import com.example.crochetPatterns.Validation.pdfFileConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostEditDTO {

    private long id;

    @NotEmpty(message = "{post.titleEmpty}")
    @Size(max = 100, message = "{post.titleTooLong}")
    private String title;

    @Size(max = 10000, message = "{post.descriptionTooLong}")
    private String description;

    @pdfFileConstraint(message = "{post.wrongFile}")  // jeśli masz taką walidację
    private MultipartFile pdfFile;

    /**
     * Bardzo ważne: zawsze inicjujemy `tagIds` pustym zestawem.
     * Dzięki temu `.contains(...)` nie rzuci NullPointerException.
     */
    private Set<Long> tagIds = new HashSet<>();
}
