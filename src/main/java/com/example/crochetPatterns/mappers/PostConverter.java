package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.PostDTO;
import com.example.crochetPatterns.dtos.PostFormDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.repositories.TagRepository;
import com.example.crochetPatterns.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostConverter {
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public PostConverter(UserRepository userRepository, TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    public Post createPost(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setPdfFilePath(postDTO.getPdfFilePath());
        User author = userRepository.findById(postDTO.getAuthorId()).get();
        post.setAuthor(author);

        Set<Tag> tags =new HashSet<>(tagRepository.findAllById(postDTO.getTagIds()));
        post.setTags(tags);
        //post.setTags((Set<Tag>) tagRepository.findAllById(postDTO.getTagIds()));
        return post;
    }
    public Post createPost(PostFormDTO postDTO , String pdfFilePath){
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setPdfFilePath(pdfFilePath);
        User author = userRepository.findById(postDTO.getAuthorId()).get();
        post.setAuthor(author);

        Set<Tag> tags =new HashSet<>(tagRepository.findAllById(postDTO.getTagIds()));
        post.setTags(tags);
        //post.setTags((Set<Tag>) tagRepository.findAllById(postDTO.getTagIds()));
        return post;
    }


    public PostDTO createDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setDescription(post.getDescription());
        postDTO.setPdfFilePath(post.getPdfFilePath());
        postDTO.setCreationDate(post.getCreationDate());
        postDTO.setAuthorId(post.getAuthor().getId());
        postDTO.setCommentIds(post.getComments().stream().map(Comment::getId).collect(Collectors.toList()));
        //postDTO.setTagIds(post.getTags().stream().map(Tag::getId).collect(Collectors.toList()));
        postDTO.setTagIds(post.getTags().stream().map(Tag::getId).collect(Collectors.toSet()));

        postDTO.setCreationTime();
        return postDTO;
    }

    public List<PostDTO> createDTO(List<Post> list) {
        List<PostDTO> listDTO = list.stream()
                .map(this::createDTO).collect(Collectors.toList());
        return listDTO;
    }

}