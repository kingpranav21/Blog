package com.ashutosh.blog.dao;

import com.ashutosh.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findUserByName(String name);
}
