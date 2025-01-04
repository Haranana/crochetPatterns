package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.TagDTO;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.repositories.PostRepository;
import com.example.crochetPatterns.repositories.TagRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TagConverter {


    private final PostRepository postRepository;

    public TagConverter(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Tag createTag(TagDTO tagDTO){
        Tag tag = new Tag();
        tag.setId(tagDTO.getId());
        tag.setName(tagDTO.getName());
        tag.setPosts((Set<Post>) postRepository.findAllById(tagDTO.getPostIds()));
        return tag;
    }

    public TagDTO createDTO(Tag tag){
        TagDTO tagDTO = new TagDTO();
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());
        tagDTO.setPostIds(tag.getPosts().stream()
                .map(Post::getId)
                .collect(Collectors.toList()));
        return tagDTO;
    }
}
