package com.example.crochetPatterns.mappers;

import com.example.crochetPatterns.dtos.PostReturnDTO;
import com.example.crochetPatterns.dtos.PostEditDTO;
import com.example.crochetPatterns.dtos.PostCreateDTO;
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

    public Post createPost(PostReturnDTO postReturnDTO) {
        Post post = new Post();
        post.setTitle(postReturnDTO.getTitle());
        post.setDescription(postReturnDTO.getDescription());
        post.setPdfFilePath(postReturnDTO.getPdfFilePath());
        User author = userRepository.findById(postReturnDTO.getAuthorId()).get();
        post.setAuthor(author);

        Set<Tag> tags =new HashSet<>(tagRepository.findAllById(postReturnDTO.getTagIds()));
        post.setTags(tags);
        //post.setTags((Set<Tag>) tagRepository.findAllById(postDTO.getTagIds()));
        return post;
    }
    public Post createPost(PostCreateDTO postDTO , String pdfFilePath){
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


    public PostReturnDTO createDTO(Post post) {
        PostReturnDTO postReturnDTO = new PostReturnDTO();
        postReturnDTO.setId(post.getId());
        postReturnDTO.setTitle(post.getTitle());
        postReturnDTO.setDescription(post.getDescription());
        postReturnDTO.setPdfFilePath(post.getPdfFilePath());
        postReturnDTO.setCreationDate(post.getCreationDate());
        postReturnDTO.setAuthorId(post.getAuthor().getId());
        postReturnDTO.setCommentIds(post.getComments().stream().map(Comment::getId).collect(Collectors.toList()));
        //postDTO.setTagIds(post.getTags().stream().map(Tag::getId).collect(Collectors.toList()));
        postReturnDTO.setTagIds(post.getTags().stream().map(Tag::getId).collect(Collectors.toSet()));

        postReturnDTO.setCreationTime();
        return postReturnDTO;
    }

    public PostCreateDTO createFormDTOFromPost(Post post) {
        PostCreateDTO dto = new PostCreateDTO();
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

    public List<PostReturnDTO> createDTO(List<Post> list) {
        List<PostReturnDTO> listDTO = list.stream()
                .map(this::createDTO).collect(Collectors.toList());
        return listDTO;
    }

}