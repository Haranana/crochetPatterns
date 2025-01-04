package com.example.crochetPatterns.services;

import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.repositories.PostRepository;
import com.example.crochetPatterns.dtos.PostDTO;
import com.example.crochetPatterns.entities.Post;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {


    public enum PostSortType{
        NEWEST,
        OLDEST,
        NAME,
        DEFAULT
    }

    private final PostRepository postRepository;

    private final PostConverter postConverter;

    public PostService(PostRepository postRepository, PostConverter postConverter) {
        this.postRepository = postRepository;
        this.postConverter = postConverter;
    }

    public void addNewPost(PostDTO postDTO){
        Post post = postConverter.createPost(postDTO);
        postRepository.save(post);
    }

    public Page<Post> getPostDTOPage(int pageId, int pageSize, PostSortType postSortType){
        Sort sort = createSortObject(postSortType);
        Pageable pageable = PageRequest.of(pageId, pageSize, sort);
        return postRepository.findAll(pageable);
    }

    public Post getPostDTO(int id){
        Optional<Post> returnPost = postRepository.findById(Integer.toUnsignedLong(id));
        return returnPost.orElse(null);
    }

    public Page<Post> getPostDTOPageByUser(int pageId, int pageSize, PostSortType postSortType, int userId){
        Sort sort = createSortObject(postSortType);
        Pageable pageable = PageRequest.of(pageId, pageSize, sort);
        return postRepository.findByAuthorId( Integer.toUnsignedLong(userId), pageable);
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

    private Sort createSortObject(PostSortType postSortType) {
        Sort returnSort = Sort.by("id").ascending();
        switch (postSortType){
            case NAME -> returnSort = Sort.by("title").ascending();
            case NEWEST -> returnSort = Sort.by("creation_date").descending();
            case OLDEST -> returnSort = Sort.by("creation_date").ascending();
            case DEFAULT -> returnSort = Sort.by("id").ascending();
        }
        return returnSort;
    }
}

