package com.ashutosh.blog.service;

import com.ashutosh.blog.dao.CommentRepository;
import com.ashutosh.blog.dao.PostRepository;
import com.ashutosh.blog.dao.UserRepository;
import com.ashutosh.blog.entity.Comments;
import com.ashutosh.blog.entity.Post;
import com.ashutosh.blog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CommentServiceImpl implements CommentService{
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;
    @Autowired
    public CommentServiceImpl(CommentRepository theCommentRepository, UserRepository theUserRepository, PostRepository thePostRepository){
        commentRepository= theCommentRepository;
        userRepository = theUserRepository;
        postRepository = thePostRepository;
    }

    @Override
    public void create(int id, Comments newComment, User user) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = localDateTime.format(dateTimeFormatter);
        Comments comment = new Comments();
        comment.setComment(newComment.getComment());
        comment.setName(user.getName());
        comment.setEmail(user.getEmail());
        comment.setCreatedAt(formattedDateTime);
        comment.setUpdatedAt(formattedDateTime);
        Post post = postRepository.findById(id).orElse(null);
        post.addComments(comment);
        commentRepository.save(comment);
    }

    @Override
    public void delete(int id) {
        Comments comment = commentRepository.findById(id).orElse(null);
        commentRepository.delete(comment);
    }

    @Override
    public void update(int id, Comments postComment, User user) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = localDateTime.format(dateTimeFormatter);
        Comments comment = commentRepository.findById(id).orElse(null);
        comment.setComment(postComment.getComment());
        comment.setUpdatedAt(formattedDateTime);
        comment.setName(user.getName());
        commentRepository.save(comment);
    }

    @Override
    public Comments findById(int id) {
        return commentRepository.findById(id).orElse(null);
    }

}
