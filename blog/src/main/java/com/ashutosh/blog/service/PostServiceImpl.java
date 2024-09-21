package com.ashutosh.blog.service;

import com.ashutosh.blog.dao.PostRepository;
import com.ashutosh.blog.dao.TagRepository;
import com.ashutosh.blog.dao.UserRepository;
import com.ashutosh.blog.entity.Comments;
import com.ashutosh.blog.entity.Post;
import com.ashutosh.blog.entity.Tag;
import com.ashutosh.blog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    @Autowired
    public PostServiceImpl(PostRepository thePostRepository, UserRepository theUserRepository, TagRepository theTagRepository){
        postRepository = thePostRepository;
        userRepository = theUserRepository;
        tagRepository = theTagRepository;
    }

    @Override
    @Transactional
    public void save(Post post, String[] tagStringArray, User user) {
//        User user=new User("ashutosh","ashutosh3@gmail.com","51545");
        // user.setId(2);
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = localDateTime.format(dateTimeFormatter);
//        User user = userRepository.findById(1).orElse(null);
        post.setAuthor(user);
        post.setTags(null);
        List<Tag> tagsInDb = tagRepository.findAll();
        Set<String> tagNameInDb = new HashSet<>();
        for(Tag tag : tagsInDb){
            tagNameInDb.add(tag.getName());
        }
        for(String tag : tagStringArray){
            if(!tagNameInDb.contains(tag)){
                Tag newTag = new Tag(tag);
                post.addTags(newTag);
            }
            else{
                Tag newTag = tagRepository.findTagByName(tag);
                post.addTags(newTag);
            }
        }
        post.setIsPublished(true);
        if(post.getId()==0){
            post.setPublishedAt(formattedDateTime);
            post.setUpdatedAt(formattedDateTime);
        }
        else{
            post.setUpdatedAt(formattedDateTime);
        }
        if(postRepository.findById(post.getId()).isPresent()){
            post.setTitle(post.getTitle());
            post.setContent(post.getContent());
        }

        String content = post.getContent();
        post.setExcerpt(content.length()>300?content.substring(0,300):content);
        postRepository.save(post);
    }

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public Post findById(int id) {
        return postRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(int id) {
        postRepository.deleteById(id);
    }

    @Override
    public void create(Model theModel) {
        Post post = new Post();
        theModel.addAttribute("post",post);
    }

    @Override
    public void update(int id, Model theModel) {
        Post post = postRepository.findById(id).orElse(null);
        theModel.addAttribute("post" , post);
        assert post != null;
        List<Tag> tags = post.getTags();
        StringBuilder tagString  = new StringBuilder();
        int i=1;
        for(Tag tag :tags){
            if(i == tags.size()){
                tagString.append(tag.getName());
            }
            else{
                tagString = new StringBuilder(tag.getName() + ",");
            }
            i++;
        }
        theModel.addAttribute("tags", tagString.toString());
    }

    @Override
    public Post read(int id, Model theModel) {
        Post post = postRepository.findById(id).orElse(null);
        theModel.addAttribute("post" , post);
        Comments comment = new Comments();
        theModel.addAttribute("Comment", comment);
        return post;
    }
    public List<Post> getListOfTitleContentTag(String data){
        data = data.trim();
        data = data.toLowerCase();
        return postRepository.findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrTagsNameContainingIgnoreCase(data, data, data);
    }
    public List<Post> getListOfSortedPosts(List<Post> posts, String sortBy){
        List<Post> sortedPosts = postRepository.getPostsSorted(sortBy);
        if(posts==null){
            return sortedPosts;
        }
        List<Post> sortedRequiredPosts = new ArrayList<>();
        for(Post sortedPost : sortedPosts){
            for(Post post : posts){
                if(post.getId() == sortedPost.getId()){
                    sortedRequiredPosts.add(post);
                }
            }
        }
        return sortedRequiredPosts;
    }
    public List<Post> getListOfFilteredPosts(List<Integer> tags, List<Integer> authors, LocalDate fromDate, LocalDate toDate, List<Post> postsPresent) {
        List<Post> publishDatesFilterPosts = new ArrayList<>();
        List<Post> authorsFilterPosts = new ArrayList<>();
        List<Post> tagsFilterPosts = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        if(fromDate!=null && toDate!=null) {
            publishDatesFilterPosts = postRepository.findByCreatedAtBetween(fromDate.atStartOfDay().toString(), toDate.atTime(LocalTime.MAX).toString());
            if(publishDatesFilterPosts.isEmpty()){
                return posts;
            }
        }
        if(authors!=null) {
            authorsFilterPosts = postRepository.findPostsByAuthorIdIn(authors);
            if(authorsFilterPosts.isEmpty()){
                return posts;
            }
        }
        if(tags!=null) {
            tagsFilterPosts = postRepository.findPostsByTagsIn(tags);
            if(tagsFilterPosts.isEmpty()){
                return posts;
            }
        }
        if(fromDate!=null && toDate!=null){
            posts.addAll(publishDatesFilterPosts);
        }
        if(authors!=null && !posts.isEmpty()){
            posts.retainAll(authorsFilterPosts);
        }
        else{
            posts.addAll(authorsFilterPosts);
        }
        if(tags!=null && !posts.isEmpty()){
            posts.retainAll(tagsFilterPosts);
        }
        else{
            posts.addAll(tagsFilterPosts);
        }
        posts.retainAll(postsPresent);
        return posts;
    }
    @Override
    public List<Post> findAll(){
        return postRepository.findAll();
    }

    @Override
    public Page<Post> getPage(List<Post> posts, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > posts.size() ? posts.size() : (start + pageable.getPageSize());
        return new PageImpl<>(posts.subList(start, end), pageable, posts.size());
    }
}
