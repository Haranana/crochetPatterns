package com.example.crochetPatterns.services;

import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.repositories.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {}

    @Test
    @DisplayName("findAllTags() - powinno zwrócić wszystkie tagi")
    void shouldReturnAllTags() {

        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("tag1");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("tag2");

        List<Tag> mockTags = Arrays.asList(tag1, tag2);
        given(tagRepository.findAll()).willReturn(mockTags);

        List<Tag> result = tagService.findAllTags();

        assertEquals(2, result.size());
        assertTrue(result.contains(tag1));
        assertTrue(result.contains(tag2));
    }

    @Test
    @DisplayName("findById() - istniejące ID -> zwraca tag, brak -> null")
    void shouldReturnTagWhenIdExists() {
        Tag tag = new Tag();
        tag.setId(10L);
        given(tagRepository.findById(10L)).willReturn(Optional.of(tag));

        Tag result = tagService.findById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    @DisplayName("findById() - nieistniejące ID -> null")
    void shouldReturnNullWhenIdDoesNotExist() {
        given(tagRepository.findById(999L)).willReturn(Optional.empty());

        Tag result = tagService.findById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("findByName() - powinno zwrócić tag wg nazwy")
    void shouldFindTagByName() {
        Tag tag = new Tag();
        tag.setName("crochet");
        given(tagRepository.findByName("crochet")).willReturn(tag);

        Tag result = tagService.findByName("crochet");

        assertNotNull(result);
        assertEquals("crochet", result.getName());
    }

    @Test
    @DisplayName("createTag() - gdy tag istnieje -> zwraca istniejący")
    void shouldReturnExistingTag() {
        Tag existing = new Tag();
        existing.setId(1L);
        existing.setName("knitting");

        given(tagRepository.findByName("knitting")).willReturn(existing);

        Tag result = tagService.createTag("knitting");

        verify(tagRepository, never()).save(any(Tag.class));
        assertEquals(existing, result);
    }

    @Test
    @DisplayName("createTag() - gdy tag nie istnieje -> tworzy nowy")
    void shouldCreateNewTag() {
        given(tagRepository.findByName("sewing")).willReturn(null);

        Tag newTag = new Tag();
        newTag.setId(2L);
        newTag.setName("sewing");

        given(tagRepository.save(any(Tag.class))).willReturn(newTag);

        Tag result = tagService.createTag("sewing");

        verify(tagRepository).save(any(Tag.class));
        assertEquals(2L, result.getId());
        assertEquals("sewing", result.getName());
    }

    @Test
    @DisplayName("saveTag() - powinno zapisać w repozytorium")
    void shouldSaveTag() {
        Tag toSave = new Tag();
        toSave.setName("myTag");

        given(tagRepository.save(toSave)).willReturn(toSave);

        Tag result = tagService.saveTag(toSave);

        verify(tagRepository).save(toSave);
        assertNotNull(result);
        assertEquals("myTag", result.getName());
    }

    @Test
    @DisplayName("findTagsByIds() - powinno zwrócić zbiór tagów dla podanych ID")
    void shouldReturnTagsByIds() {
        Tag tag1 = new Tag();
        tag1.setId(101L);

        Tag tag2 = new Tag();
        tag2.setId(102L);

        given(tagRepository.findAllById(anyCollection()))
                .willReturn(Arrays.asList(tag1, tag2));

        Set<Tag> result = tagService.findTagsByIds(new HashSet<>(Arrays.asList(101L, 102L)));

        assertEquals(2, result.size());
        assertTrue(result.contains(tag1));
        assertTrue(result.contains(tag2));
    }
}
