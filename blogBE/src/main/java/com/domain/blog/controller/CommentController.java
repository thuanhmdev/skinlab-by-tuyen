package com.domain.blog.controller;

import com.domain.blog.domain.Comment;
import com.domain.blog.domain.dto.CommentCreateDTO;
import com.domain.blog.domain.dto.CommentDTO;
import com.domain.blog.exception.DataNotFoundException;
import com.domain.blog.service.BlogService;
import com.domain.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog-api/blogs/comments")
public class CommentController {

    private final CommentService commentService;
    private final BlogService blogService;

    public CommentController(CommentService commentService, BlogService blogService) {
        this.commentService = commentService;
        this.blogService = blogService;
    }

    @GetMapping()
    public ResponseEntity<List<CommentDTO>> getComments() {
        return ResponseEntity.ok(commentService.getComments());
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByBlogId(@PathVariable("blogId") String blogId) throws DataNotFoundException {
        Boolean existBlog = blogService.existsById(blogId);
        if (!existBlog) {
            throw new DataNotFoundException("Blog does not exist");
        }
        return ResponseEntity.ok(commentService.getCommentsByBlog(blogId));
    }

    @PostMapping()
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentCreateDTO commentCreateDTO) throws DataNotFoundException {
        boolean isExistBlogOrUser = this.commentService.checkExistBlogAndUser(commentCreateDTO);
        if (!isExistBlogOrUser) {
            throw new DataNotFoundException("User/Blog not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.commentService.converseCommentDTO(this.commentService.addComment(commentCreateDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable("id") String id) throws DataNotFoundException {
        Comment getCommentById = this.commentService.getCommentById(id);
        if (getCommentById == null) {
            throw new DataNotFoundException("User/Blog not found");
        }
        this.commentService.deleteComment(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.commentService.converseCommentDTO(getCommentById));

    }

}
