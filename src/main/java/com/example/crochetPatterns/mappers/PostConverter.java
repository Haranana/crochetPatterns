package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.PostDTO;
import com.example.crochetPatterns.dtos.PostEditDTO;
import com.example.crochetPatterns.dtos.PostFormDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.repositories.TagRepository;
import com.example.crochetPatterns.repositories.UserRepository;
import jakarta.validation.constraints.Null;
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

    public PostFormDTO createFormDTOFromPost(Post post) {
        PostFormDTO dto = new PostFormDTO();
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        // PDF w PostFormDTO to MultipartFile – tutaj nie ustawiamy go (bo nie przechowujemy
        // w bazie plików binarnych).
        // Zamiast tego, jeśli chcesz, możesz przechować ścieżkę do pliku w polu tymczasowym,
        // ale nie jest to zawsze konieczne.
        dto.setAuthorId(post.getAuthor().getId());
        // ewentualnie tagIds, jeśli używasz
        // dto.setTagIds(...);

        // Jeżeli chcesz mieć w PostFormDTO pole id, to je ustaw:
        // dto.setId(post.getId());
        return dto;
    }

    public PostEditDTO createEditDTOFromPost(Post post) {
        PostEditDTO dto = new PostEditDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        // pdfFile - tutaj zwykle nie ustawiamy, bo to nowy plik, który user może uploadować

        // Kluczowy fragment:
        // Pobieramy ID wszystkich tagów z encji Post
        Set<Long> tagIds = new HashSet<>();


        tagIds = post.getTags().stream()
                .map(tag -> tag.getId())
                .collect(Collectors.toSet());

        dto.setTagIds(tagIds);

        return dto;
    }

    public List<PostDTO> createDTO(List<Post> list) {
        List<PostDTO> listDTO = list.stream()
                .map(this::createDTO).collect(Collectors.toList());
        return listDTO;
    }

}