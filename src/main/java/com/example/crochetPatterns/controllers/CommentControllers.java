package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.CommentCreateDTO;
import com.example.crochetPatterns.dtos.CommentEditDTO;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.others.LoggedUserDetails;
import com.example.crochetPatterns.repositories.CommentRepository;
import com.example.crochetPatterns.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentControllers {

    private final CommentConverter commentConverter;
    private final CommentService commentService;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentControllers(CommentConverter commentConverter, CommentService commentService, CommentRepository commentRepository) {
        this.commentConverter = commentConverter;
        this.commentService = commentService;
        this.commentRepository = commentRepository;
    }

    @RequestMapping("/writeComment")
    public String writeComment(@RequestParam int postId , Model model){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();

        CommentCreateDTO commentCreateDTO = new CommentCreateDTO();
        commentCreateDTO.setAuthorId(userDetails.getId());
        commentCreateDTO.setPostId((long) postId);

        model.addAttribute("postId" , postId);
        model.addAttribute("authorId" , userDetails.getId());
        model.addAttribute("commentFormDTO" , commentCreateDTO);
        return "writeComment";
    }

    @PostMapping("/addingComment")
    public String addingComment(@Valid @ModelAttribute("commentFormDTO") CommentCreateDTO commentCreateDTO, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("postId", commentCreateDTO.getPostId());
            return "writeComment";
        }
        commentService.addNewComment(commentConverter.createComment(commentCreateDTO));
        return "redirect:/showPost?postId=" + commentCreateDTO.getPostId();
    }

    @RequestMapping("/editComment")
    public String editComment(@RequestParam int commentId , Model model){
        Comment existingComment = commentService.getCommentDTO(commentId);
        if (existingComment == null) {
            return "redirect:/allPosts";
        }

        CommentEditDTO commentEditDTO = commentConverter.createEditDTOFromComment(existingComment);

        model.addAttribute("commentEditDTO", commentEditDTO);
        return "editComment";
    }

    @PostMapping("/confirmEditComment")
    public String submitEditComment(@Valid @ModelAttribute("commentEditDTO") CommentEditDTO commentEditDTO, BindingResult bindingResult ){
        if (bindingResult.hasErrors()) {
            return "editComment";
        }

        commentService.updateExistingComment(commentEditDTO);

        return "redirect:/showPost?postId=" + commentEditDTO.getPostId();
    }

    @RequestMapping("/deleteComment")
    public String deleteComment(@RequestParam int commentId , @RequestParam int postId, Model model){
        model.addAttribute("commentId" , commentId);
        model.addAttribute("postId" , postId);
        return "deleteCommentConfirm";
    }

    @PostMapping("/deleteCommentConfirmed")
    public String deleteCommentSuccess(@RequestParam int commentId ,@RequestParam int postId, Model model){
        commentRepository.deleteById((long) commentId); //TODO przerzucic to do serwisu
        model.addAttribute("postId" , postId);

        return "deleteCommentConfirmed";
    }
}
