package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.CommentDTO;
import com.example.crochetPatterns.dtos.PostDTO;
import com.example.crochetPatterns.dtos.UserDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.mappers.TagConverter;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.others.LoginSystem;
import com.example.crochetPatterns.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class Controllers {


    private final PostService postService;

    private final PostConverter postConverter;
    private final UserConverter userConverter;
    private final UserService userService;
    private final CommentConverter commentConverter;
    private final CommentService commentService;
    private final TagConverter tagConverter;
    private final TagService tagService;
    private final LoginSystem loginSystem;

    @Autowired
    public Controllers(PostService postService, PostConverter postConverter,
                       UserConverter userConverter, UserService userService,
                       CommentConverter commentConverter, CommentService commentService,
                       TagConverter tagConverter, TagService tagService,
                       LoginSystem loginSystem) {
        this.postService = postService;
        this.postConverter = postConverter;
        this.userConverter = userConverter;
        this.userService = userService;
        this.commentConverter = commentConverter;
        this.commentService = commentService;
        this.tagConverter = tagConverter;
        this.tagService = tagService;
        this.loginSystem = loginSystem;
    }


    @RequestMapping("/main")
    public String returnMainMenu(Model model) {
        model.addAttribute("userId" , loginSystem.getLoggedUserId());
        return "mainMenu";
    }

    @RequestMapping("/allPosts")
    public String returnAllPosts(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "2") int size,
                                 @RequestParam(defaultValue = "none") String sort,
                                 Model model) {

        Page<Post> result = postService.getPostDTOPage(page, size , PostService.PostSortType.NAME);


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
        //postService.addNewTask(new PostDTO(newPostSubject , newPostText));
        //postService.addNewPost(new PostDTO(n));
        return "successfulPost";
    }

    @RequestMapping("/showPost")
    public String showPost(@RequestParam int postId , Model model){
        Post post = postService.getPostDTO(postId);
        PostDTO postDTO = postConverter.createDTO(post);
        User tempAuthor = userService.getUserDTO(Math.toIntExact(postDTO.getAuthorId()));
        UserDTO postAuthor = userConverter.createDTO(tempAuthor);

        List<Comment> tempCommentList = commentService.getCommentDTOPageByPost(0,100, CommentService.CommentSortType.DEFAULT , postId).getContent();
        List<CommentDTO> postComments= commentConverter.createDTO( tempCommentList  );
        List<UserDTO> commentsAuthors = new ArrayList<>();
        for(CommentDTO comment : postComments){
            User tempUser = userService.getUserDTO(Math.toIntExact(comment.getAuthorId()));
            commentsAuthors.add(userConverter.createDTO(tempUser));
            commentService.updateDTOShowableDate(comment);
        }

        model.addAttribute("post" , postDTO);
        model.addAttribute("postAuthor" , postAuthor);
        model.addAttribute("postComments" , postComments);
        model.addAttribute("commentsAuthors" , commentsAuthors);
        return "showPost";
    }

    @RequestMapping("/userProfile")
    public String showUserProfile(@RequestParam int userId , Model model){
        UserDTO user = userConverter.createDTO(userService.getUserDTO(userId));
        model.addAttribute("user" , user);
        return "showUserProfile";
    }

    @RequestMapping("/userPosts")
    public String showUserPosts(@RequestParam int userId , Model model){
        UserDTO user = userConverter.createDTO(userService.getUserDTO(userId));

        List<Post> userPostsTemp = postService.getPostDTOPageByUser(0,100, PostService.PostSortType.DEFAULT , userId).getContent();
        List<PostDTO> userPosts= postConverter.createDTO( userPostsTemp  );

        model.addAttribute("user" , user);
        model.addAttribute("userPosts" , userPosts);
        return "showUserPosts";
    }

    @RequestMapping("/userComments")
    public String showUserComments(@RequestParam int userId , Model model){
        System.out.println("Start comment");

        UserDTO user = userConverter.createDTO(userService.getUserDTO(userId));

        List<Comment> userCommentsTemp = commentService.getCommentDTOPageByUser(0,100, CommentService.CommentSortType.DEFAULT , userId).getContent();
        List<CommentDTO> userComments= commentConverter.createDTO( userCommentsTemp  );
        List<PostDTO> commentedPosts = new ArrayList<>();
        for(CommentDTO comment : userComments){
            commentedPosts.add(postConverter.createDTO(postService.getPostDTO(Math.toIntExact(comment.getPostId()))));
        }
        model.addAttribute("user" , user);
        model.addAttribute("userComments" , userComments);
        model.addAttribute("commentedPosts" , commentedPosts);



        return "showUserComments";
    }

}

