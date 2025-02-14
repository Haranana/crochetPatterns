package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.PostReturnDTO;
import com.example.crochetPatterns.dtos.UserReturnDTO;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.mappers.CommentConverter;
import com.example.crochetPatterns.mappers.PostConverter;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.services.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import java.util.Collections;

@WebMvcTest(PostControllers.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class PostControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;
    @MockBean
    private PostConverter postConverter;
    @MockBean
    private UserConverter userConverter;
    @MockBean
    private UserService userService;
    @MockBean
    private CommentConverter commentConverter;
    @MockBean
    private CommentService commentService;
    @MockBean
    private TagService tagService;
    @MockBean
    private AuthService authService;
    @MockBean
    private LikeService likeService;

    @Test
    @DisplayName("GET /showPost?postId=1 -> widok showPost z modelem")
    void shouldShowPost() throws Exception {

        Post mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("Title");

        given(postService.getPost(1)).willReturn(mockPost);

        PostReturnDTO postReturnDTO = new PostReturnDTO();
        postReturnDTO.setId(1L);
        postReturnDTO.setTitle("Title");
        postReturnDTO.setAuthorId(123L);

        given(postConverter.createDTO(mockPost)).willReturn(postReturnDTO);


        given(commentService.getCommentDTOPageByPost(anyInt(), anyInt(), any(), anyInt()))
                .willReturn(new PageImpl<>(Collections.emptyList()));


        User mockUser = new User();
        mockUser.setId(123L);
        mockUser.setAvatar("/images/defaultavatar.png");
        mockUser.setUsername("fakeAuthor");

        given(userService.getUser(123L)).willReturn(mockUser);

        UserReturnDTO mockUserDTO = new UserReturnDTO();
        mockUserDTO.setId(123L);
        mockUserDTO.setUsername("fakeAuthor");
        mockUserDTO.setAvatar("/images/defaultavatar.png");

        given(userConverter.createDTO(mockUser)).willReturn(mockUserDTO);


        mockMvc.perform(get("/showPost").param("postId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("showPost"))
                .andExpect(model().attribute("post", postReturnDTO))
                .andExpect(model().attribute("postAuthor", mockUserDTO));
    }

    @Test
    @DisplayName("GET /allPosts -> widok showAllPosts")
    void shouldReturnAllPosts() throws Exception {

        Page<Post> emptyPage = new PageImpl<>(Collections.emptyList());
        given(postService.getPostDTOPage(anyInt(), anyInt(), any())).willReturn(emptyPage);
        given(tagService.findAllTags()).willReturn(Collections.emptyList());


        mockMvc.perform(get("/allPosts"))
                .andExpect(status().isOk())
                .andExpect(view().name("showAllPosts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attributeExists("allTags"));
    }

    @Test
    @DisplayName("GET /deletePost?postId=7 -> widok deletePostConfirm")
    void shouldShowDeletePostConfirm() throws Exception {
        mockMvc.perform(get("/deletePost").param("postId", "7"))
                .andExpect(status().isOk())
                .andExpect(view().name("deletePostConfirm"))
                .andExpect(model().attribute("postId", 7));
    }
}
