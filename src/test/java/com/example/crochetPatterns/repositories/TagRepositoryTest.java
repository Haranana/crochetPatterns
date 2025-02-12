package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        Tag tag = new Tag();
        tag.setName("existingTag"); // @NotEmpty
        tagRepository.save(tag);
    }

    @Test
    @DisplayName("findByName() - istniejący tag -> zwraca, brak -> null")
    void shouldFindByName() {
        // when
        Tag found = tagRepository.findByName("existingTag");
        Tag notFound = tagRepository.findByName("nonExistingTag");

        // then
        assertNotNull(found);
        assertEquals("existingTag", found.getName());
        assertNull(notFound);
    }
}
