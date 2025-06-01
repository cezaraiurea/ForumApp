package com.example.forumapp.service;

import com.example.forumapp.entity.Tag;
import com.example.forumapp.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService {
    
    @Autowired
    private TagRepository tagRepository;
    
    public Tag getOrCreateTag(String name) {
        Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(name);
        if (existingTag.isPresent()) {
            return existingTag.get();
        }
        
        Tag newTag = new Tag();
        newTag.setName(name);
        return tagRepository.save(newTag);
    }
    public Optional<Tag> findByNameIgnoreCase(String name) {
        return tagRepository.findByNameIgnoreCase(name);
    }

} 