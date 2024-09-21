package com.ashutosh.blog.dao;

import com.ashutosh.blog.entity.Post;
import com.ashutosh.blog.entity.Tag;
import com.ashutosh.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.converter.json.GsonBuilderUtils;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {

    List<Post> findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrTagsNameContainingIgnoreCase(String title, String content, String tag);
    @Query("SELECT e FROM Post e ORDER BY "+ "CASE WHEN :sortBy = 'ASC' THEN e.publishedAt END ASC, "+
            "CASE WHEN :sortBy = 'DESC' THEN e.publishedAt END DESC"
    )
    List<Post> getPostsSorted(@Param("sortBy") String sortBy);
    List<Post> findPostsByAuthorIdIn(List<Integer> author);
    List<Post> findPostsByTagsIn(List<Integer> tags);

    @Query("SELECT e FROM Post e WHERE e.publishedAt BETWEEN :fromDateTime AND :toDateTime")
    List<Post> findByCreatedAtBetween(@Param("fromDateTime") String fromDateTime, @Param("toDateTime") String toDateTime);
}
