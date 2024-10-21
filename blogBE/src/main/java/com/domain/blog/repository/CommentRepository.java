package com.domain.blog.repository;

import com.domain.blog.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String>{
    List<Comment> findByBlogIdOrderByCreatedAtDesc(String blogId);
    List<Comment> findByOrderByCreatedAtDesc();
    List<Comment> findCommentsByRootId(String rootId);
}