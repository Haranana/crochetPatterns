package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.TagReturnDTO;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TagConverter {

    public TagConverter() {}

    public Tag createTag(TagReturnDTO tagReturnDTO) {

        Tag tag = new Tag();
        tag.setName(tagReturnDTO.getName());
        return tag;
    }

    public TagReturnDTO createDTO(Tag tag) {

        TagReturnDTO dto = new TagReturnDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        if (tag.getPosts() != null) {
            dto.setPostIds(tag.getPosts().stream().map(Post::getId).collect(Collectors.toList()));
        }
        return dto;
    }
}