package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Comment;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("secret123");
        testUser.setEnabled(true);
        userRepository.save(testUser);


        testPost =new Post();
        testPost.setTitle("Sample Post");
        testPost.setPdfFilePath("dummy.pdf");
        testPost.setAuthor(testUser);
        postRepository.save(testPost);


        Comment c1= new Comment();
        c1.setAuthor(testUser);
        c1.setPost(testPost);
        c1.setText("First comment");
        commentRepository.save(c1);

        Comment c2 = new Comment();
        c2.setAuthor(testUser);
        c2.setPost(testPost);
        c2.setText("Second comment");
        commentRepository.save(c2);
    }

    @Test
    @DisplayName("findByAuthorId() - powinno zwrócić stronicowaną listę komentarzy danego autora")
    void shouldFindByAuthorId() {

        Page<Comment> result = commentRepository.findByAuthorId(testUser.getId(), PageRequest.of(0, 10)
        );

        assertEquals(2, result.getTotalElements());
        List<Comment> content = result.getContent();
        assertEquals("First comment", content.get(0).getText());
        assertEquals("Second comment", content.get(1).getText());
    }

    @Test
    @DisplayName("findByPostId() - powinno zwrócić komentarze przypisane do danego postu")
    void shouldFindByPostId() {

        Page<Comment> page = commentRepository.findByPostId(
                testPost.getId(), PageRequest.of(0, 5)
        );

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream()
                .allMatch(c -> c.getPost().equals(testPost)));
    }

    @Test
    @DisplayName("findByAuthor(User author, Pageable) - powinno znaleźć komentarze po obiekcie User")
    void shouldFindByAuthorObject() {
        Page<Comment> page = commentRepository.findByAuthor(
                testUser, PageRequest.of(0, 5)
        );

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream()
                .allMatch(c -> c.getAuthor().equals(testUser)));
    }
}
