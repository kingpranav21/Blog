package com.ashutosh.blog.dao;

import com.ashutosh.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Tag findTagByName(String Name);
}
