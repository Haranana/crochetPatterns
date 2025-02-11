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

    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }

    public Tag findById(Long id) {
        Optional<Tag> optional = tagRepository.findById(id);
        return optional.orElse(null);
    }

    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }

    public Tag createTag(String name) {
        Tag existing = tagRepository.findByName(name);
        if (existing != null) {
            return existing;
        }
        Tag newTag = new Tag();
        newTag.setName(name);
        return tagRepository.save(newTag);
    }

    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public Set<Tag> findTagsByIds(Set<Long> ids) {
        return new HashSet<>(tagRepository.findAllById(ids));
    }
}