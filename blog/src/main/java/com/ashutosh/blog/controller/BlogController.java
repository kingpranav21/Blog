package com.ashutosh.blog.controller;

import com.ashutosh.blog.entity.Post;
import com.ashutosh.blog.entity.Tag;
import com.ashutosh.blog.entity.User;
import com.ashutosh.blog.service.PostService;
import com.ashutosh.blog.service.TagService;
import com.ashutosh.blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



@Controller
public class BlogController {
    private final PostService postService;
    private final TagService tagService;
    private final UserService userService;

    @Autowired
    public BlogController(PostService thePostService, TagService theTagService, UserService theUserService) {
        postService = thePostService;
        tagService = theTagService;
        userService = theUserService;
    }
    @GetMapping("/homepage")
    public String homePage(Model theModel,
                           @RequestParam(value = "posts", required = false) List<Post> posts,
                           @RequestParam(value = "tagsChecked", required = false) List<Integer> tagsChecked,
                           @RequestParam(value= "authorsChecked", required = false) List<Integer> authorsChecked,
                           @RequestParam(value = "sort",required = false) String sortBy,
                           @RequestParam(value = "search", required = false) String data,
                           @RequestParam(value = "fromDate",required = false) LocalDate fromDate,
                           @RequestParam(value = "toDate", required = false) LocalDate toDate,
                           @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                           @RequestParam(value = "pageSize", defaultValue = "4", required = false) Integer pageSize,
                           @RequestParam(value = "hasNextPage",required = false) Boolean hasNextPage){
        if(posts != null){
            theModel.addAttribute("posts", posts);
            theModel.addAttribute("hasNextPage", hasNextPage);}
        else{
            posts = postService.findAll();
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Post> pagePosts = postService.getPage(posts,pageable);
            int totalPages = (int)(pagePosts.getTotalElements()/pageSize);
            if(totalPages % pageSize != 0){
                totalPages+=1;
            }
            hasNextPage = pageNumber<totalPages-1;
            theModel.addAttribute("posts", pagePosts.getContent());
            theModel.addAttribute("hasNextPage", hasNextPage);
        }
        if(tagsChecked != null){
            theModel.addAttribute("tagsChecked", tagsChecked);
        }
        if(authorsChecked != null){
            theModel.addAttribute("authorsChecked", authorsChecked);
        }
        if(data != null){
            theModel.addAttribute("search", data);
        }
        if(sortBy != null){
            theModel.addAttribute("sort", sortBy);
        }
        if(fromDate !=null && toDate != null){
            theModel.addAttribute("fromDate", fromDate);
            theModel.addAttribute("toDate", toDate);
        }

        List<Tag> tags = tagService.findAll();
        List<User> authors = userService.findAll();
        theModel.addAttribute("tags", tags);
        theModel.addAttribute("authors", authors);
        theModel.addAttribute("pageNumber", pageNumber);
        theModel.addAttribute("pageSize", pageSize);
        return "Home-Page";
    }
    @RequestMapping("/newpost")
    public String createBlog(Model theModel){
        postService.create(theModel);
        return "new-post";
    }
    @RequestMapping("/publish")
    public String publishBlog(HttpServletRequest request,  @ModelAttribute("post") Post post){
        String tagString = request.getParameter("tag");
        String[] tagStringArray = tagString.split(",");
        User user = userService.getCurrentUser();
        postService.save(post, tagStringArray, user);
        return "redirect:/homepage";
    }
    @GetMapping("/showblog/{postId}")
    public String showPost(@PathVariable("postId") int id, Model theModel){
        Post post = postService.read(id, theModel);
        User currentUser = userService.getCurrentUser();
        boolean postOfUser = false;
        if(currentUser != null && currentUser.equals(post.getAuthor())){
            postOfUser = true;
        }
        theModel.addAttribute("postOfUser", postOfUser);
        return "Blog-Post";
    }
    
    
    @GetMapping("/updateblog/{postId}")
    public String updatePost(@PathVariable("postId") int id, Model theModel){
        postService.update(id, theModel);
        return "new-post";
    }
    
    @GetMapping("/deleteblog/{postId}")
    public String deletePost(@PathVariable("postId") int id){
        postService.delete(id);
        return "redirect:/homepage";
    }
    @GetMapping("/posts")
    public String posts(@RequestParam(value = "search", required = false) String data,
                      @RequestParam(value = "authorsChecked", required=false) List<Integer> authorsChecked,
                      @RequestParam(value = "tagsChecked", required = false) List<Integer> tagsChecked,
                      @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                      @RequestParam(value = "toDate" , required = false) @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate toDate,
                      @RequestParam(value = "sort", required = false) String sortBy,
                      @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                      @RequestParam(value = "pageSize", defaultValue = "4", required = false) Integer pageSize,
                      @RequestParam(value = "hasNextPage",required = false) Boolean hasNextPage,
                      RedirectAttributes redirectAttributes) {
        List<Post> posts;
        if (data != null && (!data.isEmpty())) {
            posts = postService.getListOfTitleContentTag(data);
        }
        else{
            posts = postService.findAll();
        }
        if ((authorsChecked != null ) || (tagsChecked != null ) || (fromDate != null && toDate != null)) {
            posts = postService.getListOfFilteredPosts(tagsChecked, authorsChecked, fromDate, toDate, posts);
        }
        if (sortBy != null && !sortBy.isEmpty()) {
            posts = postService.getListOfSortedPosts(posts, sortBy);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Post> pagePosts = postService.getPage(posts,pageable);
        int totalPages = (int)(pagePosts.getTotalElements()/pageSize);
        if(pagePosts.getTotalElements() % pageSize != 0){
            totalPages+=1;
        }
        hasNextPage = pageNumber<totalPages-1;
        redirectAttributes.addAttribute("posts", pagePosts.getContent());
        redirectAttributes.addAttribute("tagsChecked", tagsChecked);
        redirectAttributes.addAttribute("authorsChecked", authorsChecked);
        redirectAttributes.addAttribute("search", data);
        redirectAttributes.addAttribute("sort", sortBy);
        redirectAttributes.addAttribute("fromDate", fromDate);
        redirectAttributes.addAttribute("toDate", toDate);
        redirectAttributes.addAttribute("pageNumber", pageNumber);
        redirectAttributes.addAttribute("pageSize", pageSize);
        redirectAttributes.addAttribute("hasNextPage", hasNextPage);
        return "redirect:/homepage";
    }
}
