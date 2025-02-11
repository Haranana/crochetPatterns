package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.*;
import com.example.crochetPatterns.entities.*;
import com.example.crochetPatterns.exceptions.ElementNotFoundException;
import com.example.crochetPatterns.exceptions.FileStorageException;
import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.repositories.PostRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class PostService {

    public enum PostSortType {
        TITLE_ASC,
        DATE_NEWEST,
        DATE_OLDEST,
        LIKES,
        DEFAULT
    }

    private final PostRepository postRepository;
    private final PostConverter postConverter;
    private final TagService tagService;
    private final LikeService likeService;

    public PostService(PostRepository postRepository, PostConverter postConverter, TagService tagService, LikeService likeService) {
        this.postRepository = postRepository;
        this.postConverter = postConverter;
        this.tagService = tagService;
        this.likeService = likeService;
    }

    public void addNewPost(PostDTO postDTO){
        Post post = postConverter.createPost(postDTO);
        postRepository.save(post);
    }

    public void addNewPost(PostFormDTO postFormDTO, String pdfFilePath){
        Post post = postConverter.createPost(postFormDTO, pdfFilePath);
        postRepository.save(post);
    }

    public void editPost(PostFormDTO postFormDTO, Long postId){
        // Implementacja edycji posta (opcjonalnie)
    }

    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ElementNotFoundException("Post not found: " + postId);
        }
        postRepository.deleteById(postId);
    }

    public String savePostPDF(PostFormDTO postFormDTO){
        MultipartFile pdf = postFormDTO.getPdfFile();
        try {
            String originalFilename = pdf.getOriginalFilename();
            String uniqueName = System.currentTimeMillis() + "-" + originalFilename;
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(uniqueName);
            Files.copy(pdf.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();
        } catch (IOException e) {
            throw new FileStorageException("Błąd przy zapisie pliku PDF", e);
        }
    }

    public String savePostPDF(PostEditDTO postEditDTO){
        MultipartFile pdf = postEditDTO.getPdfFile();
        try {
            String originalFilename = pdf.getOriginalFilename();
            String uniqueName = System.currentTimeMillis() + "-" + originalFilename;
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(uniqueName);
            Files.copy(pdf.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();
        } catch (IOException e) {
            throw new FileStorageException("Błąd przy zapisie pliku PDF", e);
        }
    }

    public Page<Post> getPostDTOPage(int pageId, int pageSize, PostSortType postSortType){
        Sort sort = createSortObject(postSortType);
        Pageable pageable = PageRequest.of(pageId, pageSize, sort);
        return postRepository.findAll(pageable);
    }

    public Post getPostDTO(int id){
        return postRepository.findById(Integer.toUnsignedLong(id))
                .orElseThrow(() -> new ElementNotFoundException("Post not found: " + id));
    }

    public Page<Post> getPostDTOPageByUser(int pageId, int pageSize, PostSortType postSortType, int userId){
        Sort sort = createSortObject(postSortType);
        Pageable pageable = PageRequest.of(pageId, pageSize, sort);
        return postRepository.findByAuthorId(Integer.toUnsignedLong(userId), pageable);
    }

    public List<Integer> createPageNumbers(int page, int totalPages) {
        int numbers = 3;
        List<Integer> pageNumbers = new ArrayList<>();
        if(totalPages <= numbers * 2) {
            for(int i = 0; i < totalPages; ++i) {
                pageNumbers.add(i);
            }
        } else {
            List<Integer> firstPages = new ArrayList<>();
            List<Integer> middlePages = new ArrayList<>();
            List<Integer> lastPages = new ArrayList<>();
            firstPages.add(0);
            firstPages.add(1);
            firstPages.add(2);
            lastPages.add(totalPages - 3);
            lastPages.add(totalPages - 2);
            lastPages.add(totalPages - 1);

            if((page != totalPages - 1) && (page != 0)){
                if(page - 1 > numbers) {
                    middlePages.add(-1);
                }
                middlePages.add(page - 1);
                middlePages.add(page);
                if(page + 1 < totalPages){
                    middlePages.add(page + 1);
                }
                if(page + 1 < totalPages - 3 - 1) {
                    middlePages.add(-1);
                }
            } else {
                middlePages.add(-1);
            }
            pageNumbers.addAll(firstPages);
            for(Integer i : middlePages) {
                if(i == -1) {
                    pageNumbers.add(i);
                } else if(!pageNumbers.contains(i)){
                    pageNumbers.add(i);
                }
            }
            for(Integer i : lastPages) {
                if(i == -1) {
                    pageNumbers.add(i);
                } else if(!pageNumbers.contains(i)){
                    pageNumbers.add(i);
                }
            }
        }
        return pageNumbers;
    }

    public void updateExistingPost(PostEditDTO postEditDTO){
        Post existingPost = postRepository.findById(postEditDTO.getId())
                .orElseThrow(() -> new ElementNotFoundException("Post not found: " + postEditDTO.getId()));
        existingPost.setTitle(postEditDTO.getTitle());
        existingPost.setDescription(postEditDTO.getDescription());

        if(postEditDTO.getPdfFile()!=null){
            MultipartFile newFile = postEditDTO.getPdfFile();
            if (newFile != null && !newFile.isEmpty()) {
                String newPdfPath = savePostPDF(postEditDTO);
                existingPost.setPdfFilePath(newPdfPath);
            }
        }

        Set<Tag> tags = new HashSet<>();
        for (Long tagId : postEditDTO.getTagIds()) {
            Tag tag = tagService.findById(tagId);
            if (tag != null) {
                tags.add(tag);
            }
        }
        existingPost.setTags(tags);
        postRepository.save(existingPost);
    }

    public void updateExistingPost(PostFormDTO postFormDTO, Long id) {
        Long postId = id;
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ElementNotFoundException("Post not found: " + postId));
        existingPost.setTitle(postFormDTO.getTitle());
        existingPost.setDescription(postFormDTO.getDescription());
        MultipartFile newFile = postFormDTO.getPdfFile();
        if (newFile != null && !newFile.isEmpty()) {
            String newPdfPath = savePostPDF(postFormDTO);
            existingPost.setPdfFilePath(newPdfPath);
        }
        postRepository.save(existingPost);
    }

    private Sort createSortObject(PostSortType sortType) {
        Sort defaultSort = Sort.by("id").ascending();
        switch (sortType) {
            case TITLE_ASC:
                return Sort.by("title").ascending();
            case DATE_NEWEST:
                return Sort.by("creationDate").descending();
            case DATE_OLDEST:
                return Sort.by("creationDate").ascending();
            case LIKES:
                return defaultSort;
            case DEFAULT:
            default:
                return defaultSort;
        }
    }

    public Page<Post> searchPosts(String keyword, int pageId, int pageSize, PostSortType postSortType) {
        Sort sort = createSortObject(postSortType);
        Pageable pageable = PageRequest.of(pageId, pageSize, sort);
        if (keyword == null || keyword.trim().isEmpty()) {
            return postRepository.findAll(pageable);
        }
        return postRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    public Page<Post> findByTagId(Long tagId, int page, int size, PostSortType postSortType) {
        Sort sort = createSortObject(postSortType);
        Pageable pageable = PageRequest.of(page, size, sort);
        return postRepository.findByTagId(tagId, pageable);
    }

    public Page<Post> findAllSortedByLikesInMemory(List<Post> posts, int pageId, int pageSize) {
        List<Post> mutablePosts = new ArrayList<>(posts);
        mutablePosts.sort((p1, p2) -> Long.compare(likeService.countLikes(p2.getId()), likeService.countLikes(p1.getId())));
        int total = mutablePosts.size();
        int fromIndex = pageId * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        if (fromIndex > total) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(pageId, pageSize), total);
        }
        List<Post> subList = mutablePosts.subList(fromIndex, toIndex);
        return new PageImpl<>(subList, PageRequest.of(pageId, pageSize), total);
    }

    public PostSortType mapSortParamToEnum(String sortParam) {
        return switch (sortParam) {
            case "titleAsc" -> PostSortType.TITLE_ASC;
            case "dateNewest" -> PostSortType.DATE_NEWEST;
            case "dateOldest" -> PostSortType.DATE_OLDEST;
            case "likes" -> PostSortType.LIKES;
            default -> PostSortType.DEFAULT;
        };
    }
}

