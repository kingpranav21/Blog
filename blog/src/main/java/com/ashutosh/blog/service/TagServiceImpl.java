package com.ashutosh.blog.service;

import com.ashutosh.blog.dao.TagRepository;
import com.ashutosh.blog.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;
    @Autowired
    public TagServiceImpl(TagRepository theTagRepository){
        tagRepository= theTagRepository;
    }
    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public Tag findByName(String name) {
        return tagRepository.findTagByName(name);
    }
    @Override
    public List<Tag> setTagsInNewPost(List<Tag> currentPostTags) {
        List<Tag> existingTags = new ArrayList<>();
        for (Tag tag : currentPostTags) {
            Tag existingTag = tagRepository.findTagByName(tag.getName());
            if (existingTag != null) {
                existingTags.add(existingTag);
            } else {
                existingTags.add(tag);
            }
        }
        return existingTags;
    }
}
