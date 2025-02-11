package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.*;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.others.LoggedUserDetails;
import com.example.crochetPatterns.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
public class PostControllers {
    private final PostService postService;

    private final PostConverter postConverter;
    private final UserConverter userConverter;
    private final UserService userService;
    private final CommentConverter commentConverter;
    private final CommentService commentService;
    private final AuthService authService;
    private final LikeService likeService;
    private final TagService tagService;

    @Autowired
    public PostControllers(PostService postService, PostConverter postConverter,
                           UserConverter userConverter, UserService userService,
                           CommentConverter commentConverter, CommentService commentService,
                           TagService tagService , AuthService authService , LikeService likeService) {

        this.postService = postService;
        this.postConverter = postConverter;
        this.userConverter = userConverter;
        this.userService = userService;
        this.commentConverter = commentConverter;
        this.commentService = commentService;
        this.tagService = tagService;
        this.authService = authService;
        this.likeService = likeService;
    }

    @RequestMapping("/showPost")
    public String showPost(@RequestParam int postId, Model model){

        Post post = postService.getPostDTO(postId);
        PostDTO postDTO = postConverter.createDTO(post);
        long likeCount = likeService.countLikes(post.getId());
        boolean userLiked = false;
        boolean isViewedByAuthor = false;
        boolean isLogged = authService.isLogged();
        Long loggedUserId = null;

        if(authService.isLogged()){
            loggedUserId = authService.getLoggedUserDetails().getId();
            userLiked = likeService.hasLiked(loggedUserId, post.getId());
            if (loggedUserId.equals(postDTO.getAuthorId())) {
                isViewedByAuthor = true;
            }
        }

        List<String> postTagNames = new ArrayList<>();
        for (Long tagId : postDTO.getTagIds()) {
            Tag tag = tagService.findById(tagId);
            if (tag != null) {
                postTagNames.add(tag.getName());
            }
        }

        User postAuthorEntity = userService.getUserDTO(postDTO.getAuthorId());
        UserDTO postAuthor = userConverter.createDTO(postAuthorEntity);

        List<Comment> comments = commentService.getCommentDTOPageByPost(0, 100, CommentService.CommentSortType.NEWEST, postId).getContent();
        List<CommentDTO> postComments = commentConverter.createDTO(comments);

        List<UserDTO> commentsAuthors = new ArrayList<>();
        for (CommentDTO commentDTO : postComments) {
            Long authorId = commentDTO.getAuthorId();
            if (authorId == null) {
                UserDTO deletedUser = new UserDTO();
                deletedUser.setId(0);
                deletedUser.setUsername("[user deleted]");
                deletedUser.setAvatar("/images/defaultavatar.png");
                commentsAuthors.add(deletedUser);
            } else {
                User tempUser = userService.getUserDTO(authorId.intValue());
                UserDTO userDTO = userConverter.createDTO(tempUser);
                commentsAuthors.add(userDTO);
            }
        }

        model.addAttribute("post", postDTO);
        model.addAttribute("postAuthor", postAuthor);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("userLiked", userLiked);
        model.addAttribute("isViewedByAuthor", isViewedByAuthor);
        model.addAttribute("isLogged", isLogged);
        model.addAttribute("loggedUserId", loggedUserId);
        model.addAttribute("postTagNames", postTagNames);
        model.addAttribute("postComments", postComments);
        model.addAttribute("commentsAuthors", commentsAuthors);

        return "showPost";
    }

    @RequestMapping("/allPosts")
    public String returnAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "default") String sort, // tytuł, data, polubienia
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long tagId,
            Model model) {


        model.addAttribute("allTags", tagService.findAllTags());
        PostService.PostSortType sortType = postService.mapSortParamToEnum(sort);
        Page<Post> result;

        if (tagId != null) {
            if (sortType == PostService.PostSortType.LIKES) {
                Page<Post> all = postService.findByTagId(tagId, 0, 9999, PostService.PostSortType.DEFAULT);
                result = postService.findAllSortedByLikesInMemory(all.getContent(), page, size);
            } else {
                result = postService.findByTagId(tagId, page, size, sortType);
            }
        }
        else if (search != null && !search.trim().isEmpty()) {
            if (sortType == PostService.PostSortType.LIKES) {
                Page<Post> all = postService.searchPosts(search, 0, 9999, PostService.PostSortType.DEFAULT);
                result = postService.findAllSortedByLikesInMemory(all.getContent(), page, size);
            } else {
                result = postService.searchPosts(search, page, size, sortType);
            }
        }
        else {
            if (sortType == PostService.PostSortType.LIKES) {
                Page<Post> all = postService.getPostDTOPage(0, 9999, PostService.PostSortType.DEFAULT);
                result = postService.findAllSortedByLikesInMemory(all.getContent(), page, size);
            } else {
                result = postService.getPostDTOPage(page, size, sortType);
            }
        }

        List<PostDTO> postDTOs = postConverter.createDTO(result.getContent());

        Map<Long, Long> postLikesCountMap = new HashMap<>();
        for (Post postIt : result.getContent()) {
            long likesCount = likeService.countLikes(postIt.getId());
            postLikesCountMap.put(postIt.getId(), likesCount);
        }

        Set<Long> userLikedPosts = new HashSet<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof LoggedUserDetails) {
            LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();
            Long userId = userDetails.getId();
            for (Post p : result.getContent()) {
                boolean liked = likeService.hasLiked(userId, p.getId());
                if (liked) {
                    userLikedPosts.add(p.getId());
                }
            }
        }

        model.addAttribute("posts", postDTOs);
        model.addAttribute("postLikesCountMap", postLikesCountMap);
        model.addAttribute("userLikedPosts", userLikedPosts);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("search", search);
        model.addAttribute("numbers", postService.createPageNumbers(page, result.getTotalPages()));

        return "showAllPosts";
    }

    @RequestMapping("/addPost")
    public String addPost(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();

        // Tworzymy pusty obiekt PostFormDTO
        PostFormDTO postFormDTO = new PostFormDTO();
        postFormDTO.setAuthorId(userDetails.getId());

        model.addAttribute("postFormDTO", postFormDTO);

        // Dodajemy listę wszystkich tagów z bazy (np. do multi-select w widoku)
        List<Tag> allTags = tagService.findAllTags();
        model.addAttribute("allTags", allTags);

        return "addPost";
    }

    @PostMapping("/addingPost")
    public String addPostSubmit(@Valid @ModelAttribute("postFormDTO") PostFormDTO postFormDTO, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            List<Tag> allTags = tagService.findAllTags();
            model.addAttribute("allTags", allTags);
            return "addPost";
        }

        String pdfFilePath = postService.savePostPDF(postFormDTO);
        if(!pdfFilePath.isEmpty()) {
            postService.addNewPost(postFormDTO, pdfFilePath);
            return "successfulPost";
        }

        return "mainMenu";
    }

    @GetMapping("/editPost")
    public String editPost(@RequestParam int postId, Model model) {
        // 1. Pobieramy istniejącego posta
        Post existingPost = postService.getPostDTO(postId);
        if (existingPost == null) {
            return "redirect:/allPosts";
        }

        // 2. Tworzymy PostEditDTO z wypełnionymi polami, w tym tagIds
        PostEditDTO postEditDTO = postConverter.createEditDTOFromPost(existingPost);

        // 3. Pobieramy wszystkie dostępne tagi z bazy – żeby wypełnić <select>
        List<Tag> allTags = tagService.findAllTags();
        model.addAttribute("allTags", allTags);

        // 4. Umieszczamy postEditDTO w modelu
        model.addAttribute("postEditDTO", postEditDTO);

        // 5. Zwracamy szablon Thymeleaf np. "editPost.html"
        return "editPost";
    }

    @PostMapping("/confirmEditPost")
    public String editPostSubmit(@Valid @ModelAttribute("postEditDTO") PostEditDTO postEditDTO, BindingResult bindingResult , Model model) {
        if (bindingResult.hasErrors()) {
            // ponownie dodaj listę tagów
            model.addAttribute("allTags", tagService.findAllTags());
            // i postEditDTO Spring sam zwraca w parametrach
            return "editPost";
        }

        postService.updateExistingPost(postEditDTO);
        return "redirect:/showPost?postId=" + postEditDTO.getId();
    }

    @RequestMapping("/deletePost")
    public String deletePost(@RequestParam int postId , Model model){
        model.addAttribute("postId" , postId);
        return "deletePostConfirm";
    }

    @PostMapping("/deletePostConfirmed")
    public String deletePostSuccess(@RequestParam int postId){
        postService.deletePost((long) postId);
        return "deletePostConfirmed";
    }

    @GetMapping("/posts/{id}/pdf")
    public ResponseEntity<Resource> getPostPdf(@PathVariable Long id) {
        Post post = postService.getPostDTO(Math.toIntExact(id)); // pobieramy z bazy
        String pathStr = post.getPdfFilePath();
        if (pathStr == null) {
            // brak PDF
            return ResponseEntity.notFound().build();
        }
        Path path = Paths.get(pathStr);
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Ustawiamy nagłówki Content-Type, itp.
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

}
