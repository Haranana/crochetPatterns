package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    private User userA;
    private User userB;
    private Tag tag1;
    private Tag tag2;
    private Post post1;
    private Post post2;

    @BeforeEach
    void setUp() {
        // given - tworzymy poprawnych userów
        userA = new User();
        userA.setUsername("userA");
        userA.setEmail("a@example.com");
        userA.setPassword("passA");
        userA.setEnabled(true);
        userRepository.save(userA);

        userB = new User();
        userB.setUsername("userB");
        userB.setEmail("b@example.com");
        userB.setPassword("passB");
        userB.setEnabled(true);
        userRepository.save(userB);

        // tagi
        tag1 = new Tag();
        tag1.setName("tagOne");
        tagRepository.save(tag1);

        tag2 = new Tag();
        tag2.setName("tagTwo");
        tagRepository.save(tag2);

        // post1 -> userA + tag1
        post1 = new Post();
        post1.setTitle("Crochet Pattern");
        post1.setPdfFilePath("dummy1.pdf"); // @NotEmpty
        post1.setAuthor(userA);             // @NotNull
        post1.getTags().add(tag1);
        postRepository.save(post1);

        // post2 -> userB + tag2
        post2 = new Post();
        post2.setTitle("Knitting Pattern");
        post2.setPdfFilePath("dummy2.pdf");
        post2.setAuthor(userB);
        post2.getTags().add(tag2);
        postRepository.save(post2);
    }

    @Test
    @DisplayName("findByAuthorId() - powinno zwrócić posty danego autora")
    void shouldFindByAuthorId() {
        // when
        Page<Post> pageA = postRepository.findByAuthorId(userA.getId(), PageRequest.of(0, 10));
        Page<Post> pageB = postRepository.findByAuthorId(userB.getId(), PageRequest.of(0, 10));

        // then
        assertEquals(1, pageA.getTotalElements());
        assertEquals("Crochet Pattern", pageA.getContent().get(0).getTitle());

        assertEquals(1, pageB.getTotalElements());
        assertEquals("Knitting Pattern", pageB.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase() - wyszukuje po tytule (case-insensitive)")
    void shouldFindByTitleContainingIgnoreCase() {
        // when
        Page<Post> result1 = postRepository.findByTitleContainingIgnoreCase("croch", PageRequest.of(0, 10));
        Page<Post> result2 = postRepository.findByTitleContainingIgnoreCase("pattern", PageRequest.of(0, 10));
        Page<Post> result3 = postRepository.findByTitleContainingIgnoreCase("xxx", PageRequest.of(0, 10));

        // then
        assertEquals(1, result1.getTotalElements());  // "Crochet Pattern"
        assertEquals("Crochet Pattern", result1.getContent().get(0).getTitle());

        // "pattern" jest w obu tytułach
        assertEquals(2, result2.getTotalElements());

        // "xxx" brak
        assertEquals(0, result3.getTotalElements());
    }

    @Test
    @DisplayName("findByTagId() - powinno zwrócić posty przypisane do danego tagu")
    void shouldFindByTagId() {
        // when
        Page<Post> resultTag1 = postRepository.findByTagId(tag1.getId(), PageRequest.of(0, 10));
        Page<Post> resultTag2 = postRepository.findByTagId(tag2.getId(), PageRequest.of(0, 10));
        Page<Post> resultNoTag = postRepository.findByTagId(9999L, PageRequest.of(0, 10));

        // then
        assertEquals(1, resultTag1.getTotalElements());
        assertEquals("Crochet Pattern", resultTag1.getContent().get(0).getTitle());

        assertEquals(1, resultTag2.getTotalElements());
        assertEquals("Knitting Pattern", resultTag2.getContent().get(0).getTitle());

        assertEquals(0, resultNoTag.getTotalElements());
    }
}
