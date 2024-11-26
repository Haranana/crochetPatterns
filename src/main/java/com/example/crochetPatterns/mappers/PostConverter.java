package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.PostDTO;
import com.example.crochetPatterns.entities.Post;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostConverter {

    public Post createPost(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        return post;
    }

    public PostDTO createDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setDescription(post.getDescription());
        postDTO.setCreationDate(post.getCreationDate());
        postDTO.setCreationTime();
        return postDTO;
    }

    public List<PostDTO> createDTO(List<Post> list) {
        List<PostDTO> listDTO = list.stream()
                .map(this::createDTO).collect(Collectors.toList());
        return listDTO;
    }

}