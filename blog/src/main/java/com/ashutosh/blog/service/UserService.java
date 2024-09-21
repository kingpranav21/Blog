package com.ashutosh.blog.service;

import com.ashutosh.blog.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public void save(User user);
    public User findById(int id);
    public List<User> findAll();
    public User findUserByName(String name);
    public User getCurrentUser();
}
