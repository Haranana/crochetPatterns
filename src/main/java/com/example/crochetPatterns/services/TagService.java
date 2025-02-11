package com.example.crochetPatterns.services;

import com.example.crochetPatterns.entities.Tag;
import com.example.crochetPatterns.repositories.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Zwraca listę wszystkich tagów z bazy.
     */
    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }

    /**
     * Znajdź tag po ID.
     */
    public Tag findById(Long id) {
        Optional<Tag> optional = tagRepository.findById(id);
        return optional.orElse(null);
    }

    /**
     * Znajdź tag po nazwie.
     */
    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }

    /**
     * Tworzy nowy tag w bazie (lub zwraca istniejący, jeśli nazwa jest unikalna).
     */
    public Tag createTag(String name) {
        Tag existing = tagRepository.findByName(name);
        if (existing != null) {
            return existing;
        }
        Tag newTag = new Tag();
        newTag.setName(name);
        return tagRepository.save(newTag);
    }

    /**
     * Zapisuje (aktualizuje) istniejący tag w bazie.
     */
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    /**
     * Zwraca zbiór tagów na podstawie kolekcji identyfikatorów.
     */
    public Set<Tag> findTagsByIds(Set<Long> ids) {
        return new HashSet<>(tagRepository.findAllById(ids));
    }
}