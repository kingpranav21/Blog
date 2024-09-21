package com.ashutosh.blog.service;

import com.ashutosh.blog.entity.Tag;

import java.util.List;

public interface TagService {
    public List<Tag> findAll();
    public Tag findByName(String name);
    public List<Tag> setTagsInNewPost(List<Tag> currentPostTags);
}
