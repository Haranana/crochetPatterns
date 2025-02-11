package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.*;
import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.mappers.TagConverter;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.others.LoggedUserDetails;
import com.example.crochetPatterns.others.LoginSystem;
import com.example.crochetPatterns.repositories.CommentRepository;
import com.example.crochetPatterns.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
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
public class Controllers {


    private final PostService postService;

    private final PostConverter postConverter;
    private final UserConverter userConverter;
    private final UserService userService;
    private final CommentConverter commentConverter;
    private final CommentService commentService;
    private final AuthService authService;
    private final CommentRepository commentRepository;
    private final LikeService likeService;
    private final TagConverter tagConverter;
    private final TagService tagService;

    @Autowired
    public Controllers(PostService postService, PostConverter postConverter,
                       UserConverter userConverter, UserService userService,
                       CommentConverter commentConverter, CommentService commentService,
                       TagConverter tagConverter, TagService tagService , AuthService authService ,
                       CommentRepository commentRepository,
                       LikeService likeService) {
        this.postService = postService;
        this.postConverter = postConverter;
        this.userConverter = userConverter;
        this.userService = userService;
        this.commentConverter = commentConverter;
        this.commentService = commentService;
        this.tagConverter = tagConverter;
        this.tagService = tagService;
        this.authService = authService;
        this.commentRepository = commentRepository;
        this.likeService = likeService;
    }


    @RequestMapping("/main")
    public String returnMainMenu(Model model) {

       // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();

       // model.addAttribute("userId" , userDetails.getId());
        return "mainMenu";
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

        // 1. Ustalenie typu sortowania
        PostService.PostSortType sortType = postService.mapSortParamToEnum(sort); // metoda pomocnicza (patrz niżej)

        Page<Post> result;


        // 2. Kwestia filtrowania (tagId / search). Najpierw pobieramy z bazy:
        if (tagId != null) {

            // Filtruj po tagu (bez względu na sortType – bo i tak zrobimy ewentualne sort w pamięci)
            // W przypadku sortType == LIKES pobieramy bazę np. sortowaną po ID
            if (sortType == PostService.PostSortType.LIKES) {

                // pobierz "duży" page, np. 9999, aby mieć wszystkie i posortować w pamięci
                Page<Post> all = postService.findByTagId(tagId, 0, 9999, PostService.PostSortType.DEFAULT);

                // sort i stronicowanie w pamięci
                result = postService.findAllSortedByLikesInMemory(all.getContent(), page, size);

            } else {

                // normalne sortowanie w bazie
                result = postService.findByTagId(tagId, page, size, sortType);
            }
        }
        else if (search != null && !search.trim().isEmpty()) {
            // Wyszukiwanie w tytule

            if (sortType == PostService.PostSortType.LIKES) {
                Page<Post> all = postService.searchPosts(search, 0, 9999, PostService.PostSortType.DEFAULT);
                result = postService.findAllSortedByLikesInMemory(all.getContent(), page, size);
            } else {
                result = postService.searchPosts(search, page, size, sortType);
            }
        }
        else {
            // Bez filtra tagu i bez wyszukiwania

            if (sortType == PostService.PostSortType.LIKES) {

                Page<Post> all = postService.getPostDTOPage(0, 9999, PostService.PostSortType.DEFAULT);

                result = postService.findAllSortedByLikesInMemory(all.getContent(), page, size);
            } else {
                result = postService.getPostDTOPage(page, size, sortType);
            }
        }

        // 3. Konwersja Post -> PostDTO
        List<PostDTO> postDTOs = postConverter.createDTO(result.getContent());

        // 4. Mapa: postId -> liczba polubień
        Map<Long, Long> postLikesCountMap = new HashMap<>();
        for (Post p : result.getContent()) {
            long likesCount = likeService.countLikes(p.getId());
            postLikesCountMap.put(p.getId(), likesCount);
        }

        // 5. Zbiór ID postów polubionych przez zalogowanego usera
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

        // 6. Dodajemy wszystko do modelu
        model.addAttribute("posts", postDTOs);
        model.addAttribute("postLikesCountMap", postLikesCountMap);
        model.addAttribute("userLikedPosts", userLikedPosts);



        // Numer aktualnej strony, wybrany sort, itp.
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("search", search);

        // Liczba stron po posortowaniu
        // (jeśli w pamięci, to jest w result co nam da PageImpl z totalElements)


        model.addAttribute("numbers", postService.createPageNumbers(page, result.getTotalPages()));



        return "showAllPosts";
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
    public String addPostSubmit(
            @Valid @ModelAttribute("postFormDTO") PostFormDTO postFormDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            System.out.println("Some errors have been found:");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.toString());
            });
            return "addPost";
        }

        String pdfFilePath = postService.savePostPDF(postFormDTO);
        if(!pdfFilePath.isEmpty()) {

            postService.addNewPost(postFormDTO , pdfFilePath);

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
    public String editPostSubmit(
            @Valid @ModelAttribute("postEditDTO") PostEditDTO postEditDTO,
            BindingResult bindingResult , Model model) {
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

    @RequestMapping("/showPost")
    public String showPost(@RequestParam int postId, Model model){
        // 1. Pobranie encji Post
        Post post = postService.getPostDTO(postId);
        if (post == null) {
            return "redirect:/allPosts";
        }

        // 2. Konwersja Post -> PostDTO
        PostDTO postDTO = postConverter.createDTO(post);

        // 3. Liczba polubień
        long likeCount = likeService.countLikes(post.getId());

        // 4. Logika userLiked / isViewedByAuthor oraz pobranie id zalogowanego użytkownika
        boolean userLiked = false;
        boolean isViewedByAuthor = false;
        boolean isLogged = authService.isLogged();
        Long loggedUserId = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof LoggedUserDetails) {
            LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();
            loggedUserId = userDetails.getId();
            userLiked = likeService.hasLiked(loggedUserId, post.getId());
            if (loggedUserId.equals(postDTO.getAuthorId())) {
                isViewedByAuthor = true;
            }
        }

        // 5. Tagi
        List<String> postTagNames = new ArrayList<>();
        for (Long tagId : postDTO.getTagIds()) {
            Tag t = tagService.findById(tagId);
            if (t != null) {
                postTagNames.add(t.getName());
            }
        }

        // 6. Autor posta
        User postAuthorEntity = userService.getUserDTO(Math.toIntExact(postDTO.getAuthorId()));
        UserDTO postAuthor = userConverter.createDTO(postAuthorEntity);

        // 7. Komentarze + autorzy komentarzy
        List<Comment> comments = commentService
                .getCommentDTOPageByPost(0, 100, CommentService.CommentSortType.NEWEST, postId)
                .getContent();
        List<CommentDTO> postComments = commentConverter.createDTO(comments);

        List<UserDTO> commentsAuthors = new ArrayList<>();
        for (CommentDTO commentDTO : postComments) {
            Long authorId = commentDTO.getAuthorId();
            if (authorId == null) {
                // Konto usunięte => placeholder
                UserDTO deletedUser = new UserDTO();
                deletedUser.setId(0L);
                deletedUser.setUsername("[user deleted]");
                deletedUser.setAvatar("/images/defaultavatar.png");
                commentsAuthors.add(deletedUser);
            } else {
                User tempUser = userService.getUserDTO(authorId.intValue());
                UserDTO userDTO = userConverter.createDTO(tempUser);
                commentsAuthors.add(userDTO);
            }
        }

        // 8. Dodajemy do modelu
        model.addAttribute("post", postDTO);
        model.addAttribute("postAuthor", postAuthor);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("userLiked", userLiked);
        model.addAttribute("isViewedByAuthor", isViewedByAuthor);
        model.addAttribute("isLogged", isLogged);
        model.addAttribute("loggedUserId", loggedUserId); // przekazujemy id zalogowanego użytkownika
        model.addAttribute("postTagNames", postTagNames);
        model.addAttribute("postComments", postComments);
        model.addAttribute("commentsAuthors", commentsAuthors);

        return "showPost";
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
    public String submitEditComment(@Valid @ModelAttribute("commentEditDTO") CommentEditDTO commentEditDTO,
                                    BindingResult bindingResult ){
        if (bindingResult.hasErrors()) {
            return "editComment";
        } //TODO to chyba potrzebuje tez id nw

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
        commentRepository.deleteById((long) commentId);
        model.addAttribute("postId" , postId);

        return "deleteCommentConfirmed";
    }

    @RequestMapping("/writeComment")
    public String writeComment(@RequestParam int postId , Model model){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();

        CommentFormDTO commentFormDTO = new CommentFormDTO();
        commentFormDTO.setAuthorId(userDetails.getId());
        commentFormDTO.setPostId((long) postId);

        model.addAttribute("postId" , postId);
        model.addAttribute("authorId" , userDetails.getId());
        model.addAttribute("commentFormDTO" , commentFormDTO);
        return "writeComment";
    }

    @PostMapping("/addingComment")
    public String addingComment(
            @Valid @ModelAttribute("commentFormDTO") CommentFormDTO commentFormDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            // W razie błędów dodajemy potrzebne atrybuty do modelu
            model.addAttribute("postId", commentFormDTO.getPostId());
            // Możemy również dodać inne atrybuty potrzebne dla widoku writeComment
            return "writeComment";
        }

        commentService.addNewComment(commentFormDTO);
        return "redirect:/showPost?postId=" + commentFormDTO.getPostId();
    }

    @RequestMapping("/myProfile")
    public String showLoggedUserProfile(Model model){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof LoggedUserDetails)) {
            return "login";
        }
        else {
            LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();
            model.addAttribute("user", userConverter.createDTO(userService.getUserDTO(userDetails.getId())));
            System.out.println(userConverter.createDTO(userService.getUserDTO(userDetails.getId())).getAvatar());
            model.addAttribute("isViewedByAuthor" , true);
            return "showUserProfile";
        }
    }

    /*
    @RequestMapping("/userProfile/{userId}")
    public String showUserProfile(@PathVariable int userId, Model model) {
        UserDTO user = userConverter.createDTO(userService.getUserDTO(userId));
        model.addAttribute("user", user);
        return "showUserProfile";
    }
    */

    @RequestMapping("/userProfile")
    public String showUserProfile(@RequestParam int userId , Model model){
        UserDTO user = userConverter.createDTO(userService.getUserDTO(userId));

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
        UserDTO user = userConverter.createDTO(userService.getUserDTO(userId));

        List<Post> userPostsTemp = postService.getPostDTOPageByUser(0,100, PostService.PostSortType.DEFAULT , userId).getContent();
        List<PostDTO> userPosts= postConverter.createDTO( userPostsTemp  );

        model.addAttribute("user" , user);
        model.addAttribute("userPosts" , userPosts);
        return "showUserPosts";
    }

    @RequestMapping("/userComments")
    public String showUserComments(@RequestParam int userId , Model model){

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

    // GET /editProfile – wyświetlenie formularza z danymi aktualnego użytkownika
    @GetMapping("/editProfile")
    public String editProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();
        User user = userService.getUserDTO(userDetails.getId());

        // Konwersja encji do DTO edycji – upewnij się, że w UserConverter masz metodę createEditDTO(User user)
        UserEditDTO userEditDTO = userConverter.createEditDTO(user);
        model.addAttribute("userEditDTO", userEditDTO);
        return "editProfile";  // nazwa szablonu, np. editProfile.html
    }

    // POST /confirmEditProfile – przetwarzanie formularza edycji profilu
    @PostMapping("/confirmEditProfile")
    public String confirmEditProfile(@Valid @ModelAttribute("userEditDTO") UserEditDTO userEditDTO,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "editProfile";
        }
        userService.updateUserProfile(userEditDTO);
        return "redirect:/userProfile?userId=" + userEditDTO.getId();
    }

    // GET – wyświetlenie potwierdzenia usunięcia konta
    @GetMapping("/deleteAccount")
    public String deleteAccount(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof LoggedUserDetails)) {
            return "login"; // jeśli użytkownik nie jest zalogowany
        }
        LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();
        model.addAttribute("userId", userDetails.getId());
        return "deleteAccountConfirm"; // szablon potwierdzenia usunięcia konta
    }

    // POST – wykonanie usunięcia konta
    @PostMapping("/deleteAccountConfirmed")
    public String deleteAccountConfirmed(@RequestParam Long userId, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof LoggedUserDetails)) {
            return "login";
        }
        LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();
        if (!userDetails.getId().equals(userId)) {
            // Można tu dodać komunikat o błędzie, jeśli ktoś próbuje usunąć konto innego użytkownika
            return "error";
        }
        /*
        // Usuń konto
        userService.deleteUser(userId);
        // Unieważnij sesję i wyczyść kontekst bezpieczeństwa
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        */
        System.out.println("Konto usunieto");
        return "deleteAccountSuccess"; // szablon potwierdzający usunięcie konta
    }

    @PostMapping("/post/{postId}/like")
    public String likePost(@PathVariable("postId") Long postId) {
        // Sprawdzamy, czy jest zalogowany
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof LoggedUserDetails)) {
            return "redirect:/login";
        }
        LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();
        Long userId = userDetails.getId();

        likeService.likePost(userId, postId);

        // Po polubieniu wracamy do szczegółów posta
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

