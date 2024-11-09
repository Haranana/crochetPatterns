package com.example.crochetPatterns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controllers {

    private final Services service;

    @Autowired
    public Controllers(Services service) {
        this.service = service;
    }

    @RequestMapping("/main")
    public String returnMainMenu() {
        return "mainMenu";
    }

    @RequestMapping("/allPosts")
    public String returnAllPosts(Post post, Model model) {
        model.addAttribute("posts" , service.getPosts());
        return "showAllPosts";
    }

    @RequestMapping("/addPost")
    public String addPost(Post post) {
        return "addPost";
    }

    @RequestMapping("/addingPost")
    public String addPost(@RequestParam String newPostSubject , @RequestParam String newPostText) {
        service.addPost(new Post(newPostSubject , newPostText));
        return "successfulPost";
    }
}
