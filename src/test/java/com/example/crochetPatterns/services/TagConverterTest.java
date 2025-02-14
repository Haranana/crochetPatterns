package com.example.crochetPatterns.services;


import com.example.crochetPatterns.dtos.TagReturnDTO;
import com.example.crochetPatterns.entities.Post;
import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.mappers.TagConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TagConverterTest {

    private TagConverter tagConverter;

    @BeforeEach
    void setUp() {
        tagConverter = new TagConverter();
    }

    @Test
    void shouldCreateTagFromTagReturnDTO() {
        TagReturnDTO dto = new TagReturnDTO();
        dto.setName("testTag");

        Tag tag = tagConverter.createTag(dto);

        assertNotNull(tag);
        assertEquals("testTag", tag.getName());
    }

    @Test
    void shouldCreateTagReturnDTOFromTag() {

        Tag tag = new Tag();
        tag.setId(10L);
        tag.setName("tagName");
        Post p1 = new Post(); p1.setId(101L);
        Post p2 = new Post(); p2.setId(202L);

        Set<Post> set = new HashSet<>();
        set.add(p1);
        set.add(p2);
        tag.setPosts(set);


        TagReturnDTO dto = tagConverter.createDTO(tag);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("tagName", dto.getName());
        assertEquals(2, dto.getPostIds().size());
        assertTrue(dto.getPostIds().contains(101L));
    }
}