package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.PostReturnDTO;
import com.example.crochetPatterns.dtos.PostEditDTO;
import com.example.crochetPatterns.dtos.PostCreateDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.services.TagService;
import com.example.crochetPatterns.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostConverter {

    private final UserService userService;
    private final TagService tagService;

    @Autowired
    public PostConverter(UserService userService, TagService tagService) {
        this.userService = userService;
        this.tagService = tagService;
    }

    public Post createPost(PostReturnDTO postReturnDTO) {

        Post post = new Post();
        post.setTitle(postReturnDTO.getTitle());
        post.setDescription(postReturnDTO.getDescription());
        post.setPdfFilePath(postReturnDTO.getPdfFilePath());
        User author = userService.getUser(postReturnDTO.getAuthorId());
        post.setAuthor(author);

        Set<Tag> tags = new HashSet<>(tagService.findTagsByIds(postReturnDTO.getTagIds()));
        post.setTags(tags);
        return post;
    }

    public Post createPost(PostCreateDTO postDTO, String pdfFilePath) {

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setPdfFilePath(pdfFilePath);
        User author = userService.getUser(postDTO.getAuthorId());
        post.setAuthor(author);

        Set<Tag> tags = new HashSet<>(tagService.findTagsByIds(postDTO.getTagIds()));
        post.setTags(tags);
        return post;
    }

    public PostReturnDTO createDTO(Post post) {

        PostReturnDTO postReturnDTO = new PostReturnDTO();
        postReturnDTO.setId(post.getId());
        postReturnDTO.setTitle(post.getTitle());
        postReturnDTO.setDescription(post.getDescription());
        postReturnDTO.setPdfFilePath(post.getPdfFilePath());
        postReturnDTO.setCreationDate(post.getCreationDate());
        postReturnDTO.setAuthorId(post.getAuthor().getId());
        postReturnDTO.setCommentIds(post.getComments().stream().map(Comment::getId).collect(Collectors.toList()));
        postReturnDTO.setTagIds(post.getTags().stream().map(Tag::getId).collect(Collectors.toSet()));

        postReturnDTO.setCreationTime();
        return postReturnDTO;
    }

    public PostCreateDTO createFormDTOFromPost(Post post) {

        PostCreateDTO dto = new PostCreateDTO();
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setAuthorId(post.getAuthor().getId());
        return dto;
    }

    public PostEditDTO createEditDTOFromPost(Post post) {

        PostEditDTO dto = new PostEditDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        Set<Long> tagIds = post.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        dto.setTagIds(tagIds);
        return dto;
    }

    public List<PostReturnDTO> createDTO(List<Post> list) {

        return list.stream().map(this::createDTO).collect(Collectors.toList());
    }
}