package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.TagReturnDTO;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.repositories.PostRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TagConverter {

    private final PostRepository postRepository;

    public TagConverter(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Konwersja z TagDTO -> Tag.
     * Uwaga: w tym prostym przykładzie, jeśli dany Tag istnieje w bazie,
     * powinniśmy raczej go pobierać z TagService zamiast tworzyć nowy.
     */
    public Tag createTag(TagReturnDTO tagReturnDTO) {
        Tag tag = new Tag();
        tag.setName(tagReturnDTO.getName());
        // w postach raczej nie ustawiamy od razu, bo
        // zarządza tym relacja w Post -> Tag
        return tag;
    }

    /**
     * Konwersja z Tag -> TagDTO.
     * Ustawiamy listę ID postów powiązanych z tym tagiem.
     */
    public TagReturnDTO createDTO(Tag tag) {
        TagReturnDTO dto = new TagReturnDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        // Ustawiamy powiązane posty
        dto.setPostIds(tag.getPosts().stream().map(Post::getId).collect(Collectors.toList()));
        return dto;
    }
}