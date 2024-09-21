package com.ashutosh.blog.service;

import com.ashutosh.blog.entity.Comments;
import com.ashutosh.blog.entity.User;
import org.springframework.ui.Model;

public interface CommentService {
    public void create(int id, Comments comment, User user);
    public void delete(int id);
    public void update(int id, Comments postComment, User user);
    public Comments findById(int id);
}
