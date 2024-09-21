package com.ashutosh.blog.controller;

import com.ashutosh.blog.entity.Comments;
import com.ashutosh.blog.entity.Post;
import com.ashutosh.blog.entity.User;
import com.ashutosh.blog.service.CommentService;
import com.ashutosh.blog.service.PostService;
import com.ashutosh.blog.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class CommentController {
    private CommentService commentService;
    private PostService postService;
    private UserService userService;
    @Autowired
    public CommentController(CommentService theCommentService, PostService thePostService, UserService theUserService){
        commentService = theCommentService;
        postService = thePostService;
        userService = theUserService;
    }
    @PostMapping("/comments/{postId}/{commentId}")
    public String addComment(@PathVariable("postId") int postId, @PathVariable("commentId") int commentId, @ModelAttribute("Comment") Comments postComment){
        User user= userService.getCurrentUser();
        if(commentId != 0){
            commentService.update(commentId, postComment, user);
            return "redirect:/showblog/" + postId;
        }
        else {
            commentService.create(postId, postComment, user);
            return "redirect:/showblog/" + postId;
        }
    }
    @PostMapping("/delete-comment/{postId}/{commentId}")
    public String deleteComment(@PathVariable("commentId") int commentId, @PathVariable("postId") int postId){
        commentService.delete(commentId);
        return "redirect:/showblog/"+ postId;
    }

    @PostMapping("/edit-comment/{postId}/{commentId}")
    public String updateComment(@PathVariable("commentId") int commentId, @PathVariable("postId") int postId, Model theModel){
        Comments comment = commentService.findById(commentId);
        Post post = postService.findById(postId);
        String commentString = comment.getComment();
        theModel.addAttribute("commentString", commentString);
        theModel.addAttribute("post",post );
        theModel.addAttribute("Comment", comment);
        return "Blog-Post";
    }

}
