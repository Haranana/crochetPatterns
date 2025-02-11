package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.CommentReturnDTO;
import com.example.crochetPatterns.dtos.PostReturnDTO;
import com.example.crochetPatterns.dtos.UserReturnDTO;
import com.example.crochetPatterns.dtos.UserEditDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.others.LoggedUserDetails;
import com.example.crochetPatterns.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class UserProfileControllers {

    private final PostService postService;
    private final PostConverter postConverter;
    private final UserConverter userConverter;
    private final UserService userService;
    private final CommentConverter commentConverter;
    private final CommentService commentService;
    private final AuthService authService;
    private final LikeService likeService;

    @Autowired
    public UserProfileControllers(PostService postService, PostConverter postConverter, UserConverter userConverter, UserService userService,
                                  CommentConverter commentConverter, CommentService commentService, AuthService authService, LikeService likeService) {
        this.postService = postService;
        this.postConverter = postConverter;
        this.userConverter = userConverter;
        this.userService = userService;
        this.commentConverter = commentConverter;
        this.commentService = commentService;
        this.authService = authService;
        this.likeService = likeService;
    }

    @RequestMapping("/myProfile")
    public String showLoggedUserProfile(Model model){

        if(authService.isLogged()){
            model.addAttribute("user", userConverter.createDTO(userService.getUser(authService.getLoggedUserDetails().getId())));
            System.out.println(userConverter.createDTO(userService.getUser(authService.getLoggedUserDetails().getId())).getAvatar());
            model.addAttribute("isViewedByAuthor" , true);
            return "showUserProfile";
        }else{
            return "login";
        }
    }

    @RequestMapping("/userProfile")
    public String showUserProfile(@RequestParam int userId , Model model){

        UserReturnDTO user = userConverter.createDTO(userService.getUser(userId));

        if(authService.isLogged() && userId == authService.getLoggedUserDetails().getId() ){
            model.addAttribute("isViewedByAuthor" , true);
        }
        else{
            model.addAttribute("isViewedByAuthor" , false);
        }

        model.addAttribute("user" , user);
        return "showUserProfile";
    }

    @RequestMapping("/userPosts")
    public String showUserPosts(@RequestParam int userId , Model model){
        UserReturnDTO user = userConverter.createDTO(userService.getUser(userId));

        List<Post> userPostsTemp = postService.getPostPageByUser(0,100, PostService.PostSortType.DATE_NEWEST , userId).getContent();
        List<PostReturnDTO> userPosts= postConverter.createDTO( userPostsTemp  );

        model.addAttribute("user" , user);
        model.addAttribute("userPosts" , userPosts);
        return "showUserPosts";
    }

    @RequestMapping("/userComments")
    public String showUserComments(@RequestParam int userId , Model model){

        UserReturnDTO user = userConverter.createDTO(userService.getUser(userId));

        List<Comment> userCommentsTemp = commentService.getCommentDTOPageByUser(0,100, CommentService.CommentSortType.NEWEST , userId).getContent();
        List<CommentReturnDTO> userComments= commentConverter.createDTO(userCommentsTemp);
        List<PostReturnDTO> commentedPosts = new ArrayList<>();
        for(CommentReturnDTO comment : userComments){
            commentedPosts.add(postConverter.createDTO(postService.getPost(Math.toIntExact(comment.getPostId()))));
        }
        model.addAttribute("user" , user);
        model.addAttribute("userComments" , userComments);
        model.addAttribute("commentedPosts" , commentedPosts);

        return "showUserComments";
    }

    @GetMapping("/editProfile")
    public String editProfile(Model model) {

        if(!authService.isLogged()){
            return "login";
        }

        User user = userService.getUser(authService.getLoggedUserDetails().getId());
        UserEditDTO userEditDTO = userConverter.createEditDTO(user);
        model.addAttribute("userEditDTO", userEditDTO);

        return "editProfile";
    }

    @PostMapping("/confirmEditProfile")
    public String confirmEditProfile(@Valid @ModelAttribute("userEditDTO") UserEditDTO userEditDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "editProfile";
        }

        userService.updateUserProfile(userEditDTO);
        return "redirect:/userProfile?userId=" + userEditDTO.getId();
    }

    @GetMapping("/deleteAccount")
    public String deleteAccount(Model model) {

        if(!authService.isLogged()){
            return "login";
        }

        model.addAttribute("userId", authService.getLoggedUserDetails().getId());
        return "deleteAccountConfirm";
    }

    @PostMapping("/deleteAccountConfirmed")
    public String deleteAccountConfirmed(@RequestParam Long userId, HttpServletRequest request) {

        if(!authService.isLogged() || !Objects.equals(authService.getLoggedUserDetails().getId(), userId)){
            throw new AccessDeniedException("Access Denied");
        }

        //TODO zamienic w final realease
        /*
        //usuniecie konta, uniewaznienie sesji i kontekstu bezpieczenstwa
        userService.deleteUser(userId);
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        */

        System.out.println("Konto usunieto");

        return "deleteAccountSuccess";
    }

    @PostMapping("/post/{postId}/like")
    public String likePost(@PathVariable("postId") Long postId) {

        if(!authService.isLogged()){
            return "redirect:/login";
        }

        likeService.likePost(authService.getLoggedUserDetails().getId(), postId);

        return "redirect:/showPost?postId=" + postId;
    }

    @PostMapping("/post/{postId}/unlike")
    public String unlikePost(@PathVariable("postId") Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof LoggedUserDetails)) {
            return "redirect:/login";
        }
        LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();

        likeService.unlikePost(userId, postId);

        return "redirect:/showPost?postId=" + postId;
    }
}
