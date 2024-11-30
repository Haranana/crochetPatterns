package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.PostDTO;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.services.PostService;
import com.example.crochetPatterns.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controllers {


    private final PostService postService;

    private final PostConverter postConverter;

    @Autowired
    public Controllers(PostService postService, PostConverter postConverter) {
        this.postService = postService;
        this.postConverter = postConverter;
    }

    @RequestMapping("/main")
    public String returnMainMenu() {
        return "mainMenu";
       // return "debug";
    }
/*
    @RequestMapping("/allPosts")
    public String returnAllPosts(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 @RequestParam(defaultValue = "none") String sort,
                                 Model model) {

        Page<Post> result = postService.getAllDTO(page, size , sort);


        model.addAttribute("posts", postConverter.createDTO(result.getContent()));
        model.addAttribute("page" , page);
        model.addAttribute("sort" , sort);
        model.addAttribute("size" , size);
        model.addAttribute("numbers", postService.createPageNumbers(page, result.getTotalPages()));

        return "showAllPosts";
    }

    @RequestMapping("/addPost")
    public String addPost(Post post) {
        return "addPost";
    }

    @RequestMapping("/addingPost")
    public String addPost(@RequestParam String newPostSubject , @RequestParam String newPostText) {
        postService.addNewTask(new PostDTO(newPostSubject , newPostText));
        return "successfulPost";
    }

    @RequestMapping("/showPost")
    public String showPost(@RequestParam int postId , Model model){
        Post post = postService.getPostDTO(postId);
        model.addAttribute("post" , postConverter.createDTO(post));
        return "showPost";
    }

    @RequestMapping("/user")
    public String showUserProfile(@RequestParam int userId , Model model){
        return "showUserProfile";
    }
    */

}

