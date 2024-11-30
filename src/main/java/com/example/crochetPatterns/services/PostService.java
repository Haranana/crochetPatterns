package com.example.crochetPatterns.services;

import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.repositories.PostRepository;
import com.example.crochetPatterns.dtos.PostDTO;
import com.example.crochetPatterns.entities.Post;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    int numbers = 3;

    private final PostRepository postRepository;

    private final PostConverter postConverter;

    public PostService(PostRepository postRepository, PostConverter postConverter) {
        this.postRepository = postRepository;
        this.postConverter = postConverter;
    }

    public void addNewTask(PostDTO postDTO) {
        Post post = postConverter.createPost(postDTO);
        postRepository.save(post);
    }

    public Post getPostDTO(int id){
        Post examplePost = new Post();
        examplePost.setId(Integer.toUnsignedLong(id));

        return postRepository.findAll(Example.of(examplePost)).get(0);
    }

    public List<Post> getAll() {
        return postRepository.findAll();
    }

    public List<Integer> createPageNumbers(int page, int totalPages) {
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

    public Page<Post> getAllDTO(int page, int size) {
        Pageable pageable  = PageRequest.of(page, size, Sort.by("id").ascending());
        System.out.printf(postRepository.findAll(pageable).getContent().toString());
        return postRepository.findAll(pageable);
    }

    public Page<Post> getAllDTO(int page, int size, String sort) {
        Pageable pageable;
        Sort sortowanie;
        if("none".equals(sort)) {
            sortowanie = Sort.by("id").ascending();;
        } else if("sub".equals(sort)) {
            sortowanie = Sort.by("subject").ascending();
        } else {
            sortowanie = Sort.by("creationDate").descending();
        }
        pageable = PageRequest.of(page, size, sortowanie);
        return postRepository.findAll(pageable);
    }
}
