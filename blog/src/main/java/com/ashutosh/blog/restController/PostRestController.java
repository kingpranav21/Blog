package com.ashutosh.blog.restController;

import com.ashutosh.blog.dao.PostRepository;
import com.ashutosh.blog.entity.Post;
import com.ashutosh.blog.entity.Tag;
import com.ashutosh.blog.entity.User;
import com.ashutosh.blog.service.PostService;
import com.ashutosh.blog.service.TagService;
import com.ashutosh.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostRestController {
    private final PostService postService;
    private final PostRepository postRepository;
    private final TagService tagService;
    private final UserService userService;

    @Autowired
    public PostRestController(PostService thePostService, TagService theTagService, UserService theUserService, PostRepository thePostRepository) {
        postService = thePostService;
        tagService = theTagService;
        userService = theUserService;
        postRepository = thePostRepository;
    }
    @GetMapping("/posts")
    public List<Post> posts(){
        return postService.findAll();
    }
    @GetMapping("/posts/{postid}")
    public Post post(@PathVariable("postid") Integer id){
        return postService.findById(id);
    }

    @PostMapping("/posts")
    public Post create(@RequestBody Post post){
        postRepository.save(post);
        return post;
    }
    @PutMapping("/posts/{postId}")
    public String updatePost(@PathVariable("postId") Integer postId , @RequestBody Post updatedPost, Authentication authentication){
        Post existingPost = postService.findById(postId);
        String currentLoggedInUserName = authentication.getName();
        String postAuthorName = existingPost.getAuthor().getName();
        if(!postAuthorName.equals(currentLoggedInUserName) &&
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return "access denied";
        }

        System.out.println("Saving");
        updatedPost.setId(existingPost.getId());
        updatedPost.setAuthor(existingPost.getAuthor());
        postRepository.save(updatedPost);
        return "success";
    }
    @DeleteMapping("/posts/{postId}")
    public String delete(@PathVariable("postId") Integer id){
        Post post  = postService.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String loggedInUser = authentication.getName();
         User user = userService.findUserByName(loggedInUser);
        if(!post.getAuthor().getName().equals(loggedInUser) &&
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return "access denied";
        }
        postService.delete(id);
        return "post is deleted of id "+id;
    }
}
