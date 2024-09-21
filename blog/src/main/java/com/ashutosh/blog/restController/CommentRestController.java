package com.ashutosh.blog.restController;

import com.ashutosh.blog.entity.Comments;
import com.ashutosh.blog.entity.Post;
import com.ashutosh.blog.entity.User;
import com.ashutosh.blog.service.CommentService;
import com.ashutosh.blog.service.PostService;
import com.ashutosh.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentRestController {
    private CommentService commentService;
    private PostService postService;
    private UserService userService;
    @Autowired
    public CommentRestController(CommentService theCommentService, PostService thePostService, UserService theUserService){
        commentService = theCommentService;
        postService = thePostService;
        userService = theUserService;
    }
    @PostMapping("/comments/{postId}")
    public String addComment(@PathVariable("postId") Integer id, @RequestBody Comments commentRequest,  Authentication authentication){
        Post post = postService.findById(id);
        String currentLoggedInUserName = authentication.getName();
        String postAuthorName = post.getAuthor().getName();
        if(!postAuthorName.equals(currentLoggedInUserName) &&
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return "access denied";
        }
        Comments comment = new Comments();
        comment.setComment(commentRequest.getComment());
        User user = post.getAuthor();
        commentService.create(id, comment, user);
        return "comment saved!!";

    }
    @GetMapping("/comments/{postId}")
    public List<Comments> comment(@PathVariable("postId") Integer id){
        Post post = postService.findById(id);
        return post.getComments();
    }
    @PutMapping("/comments/{commentId}")
    public String updateComment(@PathVariable("commentId") Integer id, @RequestBody Comments comment, Authentication authentication ){
        String currentLoggedInUserName = authentication.getName();
        String commentAuthorName = comment.getName();
        if(!commentAuthorName.equals(currentLoggedInUserName) &&
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return "access denied";
        }
        User user = userService.findUserByName(commentAuthorName);
        commentService.update(id, comment, user);
        return "comment updated";
    }
    @DeleteMapping("/comments/{commentId}")
    public String deleteComment(@PathVariable("commentId") Integer id, Authentication authentication){
        Comments comment = commentService.findById(id);
        String currentLoggedInUserName = authentication.getName();
        String commentAuthorName = comment.getName();
        if(!commentAuthorName.equals(currentLoggedInUserName) &&
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return "access denied";
        }
        commentService.delete(id);
        return "deleted";
    }
}
