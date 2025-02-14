package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.UserRegistrationDTO;
import com.example.crochetPatterns.dtos.UserReturnDTO;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.services.PostService;
import com.example.crochetPatterns.services.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserConverterTest {

    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;

    @InjectMocks
    private UserConverter userConverter;

    @BeforeEach
    void setUp() {}

    @Test
    void shouldCreateUserFromUserRegistrationDTO() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("testUser");
        dto.setEmail("test@example.com");
        String encodedPassword = "encodedPW";


        User user = userConverter.createUser(dto, encodedPassword);


        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("encodedPW", user.getPassword());
        assertFalse(user.isEnabled());
        assertEquals("/images/defaultavatar.png", user.getAvatar());
    }

    @Test
    void shouldCreateUserFromUserReturnDTO() {

        UserReturnDTO userReturnDTO = new UserReturnDTO();
        userReturnDTO.setUsername("bob");
        userReturnDTO.setEmail("bob@example.com");
        userReturnDTO.setPassword("pwd");
        userReturnDTO.setAvatar("avatarPath");
        userReturnDTO.setBio("someBio");
        userReturnDTO.setEnabled(true);
        userReturnDTO.setPostIds(Collections.singletonList(1L));
        userReturnDTO.setCommentIds(Collections.singletonList(2L));


        given(postService.findPostsByIds(Collections.singletonList(1L)))
                .willReturn(Collections.emptyList());
        given(commentService.findCommentsByIds(Collections.singletonList(2L)))
                .willReturn(Collections.emptyList());


        User user = userConverter.createUser(userReturnDTO);


        assertNotNull(user);
        assertEquals("bob", user.getUsername());
        assertEquals("bob@example.com", user.getEmail());
        assertEquals("pwd", user.getPassword());
        assertTrue(user.isEnabled());
    }
}
