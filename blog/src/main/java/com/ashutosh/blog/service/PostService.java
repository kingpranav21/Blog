package com.ashutosh.blog.service;

import com.ashutosh.blog.entity.Post;
import com.ashutosh.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

public interface PostService{
    public void save(Post post, String[] tagStringArray, User user);
    public void save(Post post);
    public Post findById(int id);

    public void delete(int id);

    public void create(Model theModel);

    public void update(int id, Model theModel);

    public Post read(int id, Model theModel);

    public List<Post> getListOfTitleContentTag(String data);
    public List<Post> getListOfSortedPosts(List<Post> posts, String sortBy);

    public List<Post> getListOfFilteredPosts(List<Integer> tags, List<Integer> author, LocalDate fromDate, LocalDate toDate, List<Post> postsPresent);

    public List<Post> findAll();

    public Page<Post> getPage(List<Post> posts, Pageable pageable);

}
