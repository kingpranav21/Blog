package com.ashutosh.blog.dao;

import com.ashutosh.blog.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comments, Integer> {

}
