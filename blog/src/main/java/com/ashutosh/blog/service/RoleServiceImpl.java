package com.ashutosh.blog.service;

import com.ashutosh.blog.dao.RoleRepository;
import com.ashutosh.blog.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService{
    private RoleRepository roleRepository;
    @Autowired
    public RoleServiceImpl(RoleRepository theRoleRepository){
        roleRepository = theRoleRepository;
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
    }
}
