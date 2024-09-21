package com.ashutosh.blog.controller;

import com.ashutosh.blog.entity.Role;
import com.ashutosh.blog.entity.User;
import com.ashutosh.blog.service.RoleService;
import com.ashutosh.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private UserService userService;
    private RoleService roleService;
    @Autowired
    public RegistrationController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @GetMapping("/registrationForm")
    public String showMyLoginPage(Model theModel) {
        User user = new User();
        theModel.addAttribute("user", user);
        return "Registration-Page";
    }
    @PostMapping("/submitRegistrationForm")
    public String processRegistrationForm(
            @ModelAttribute("user") User theUser,
            Model theModel) {
        String name = theUser.getName();
        boolean isUserExists = false;
        User exsistingUser = userService.findUserByName(name);
        if(exsistingUser!=null){
            isUserExists = true;
            theModel.addAttribute("isUserExists", isUserExists);
            return "redirect:/showRegistrationForm";
        }
        Role role = new Role();
        role.setRole("AUTHOR");
        role.setUsername(theUser.getName());
        theUser.setPassword("{noop}"+theUser.getPassword());
        roleService.saveRole(role);
        userService.save(theUser);
        theModel.addAttribute("userCreated", true);
        return "redirect:/login";
    }
}
