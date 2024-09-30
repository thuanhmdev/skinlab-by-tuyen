package com.domain.blog.controller;

import com.domain.blog.annotation.ApiMessage;
import com.domain.blog.domain.Blog;
import com.domain.blog.domain.dto.BlogDTO;
import com.domain.blog.domain.dto.ResultPaginationDTO;
import com.domain.blog.exception.DataNotFoundException;
import com.domain.blog.service.BlogService;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/blog-api")
public class BlogController {
    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/blogs")
    public ResponseEntity<List<BlogDTO>> getBlogs() {
        return ResponseEntity.ok(this.blogService.getBlogs());
    }

    @GetMapping("/blogs-pagination")
    public ResponseEntity<ResultPaginationDTO> getBlogsClientHavePagination(@Filter Specification<Blog> spec, Pageable pageable) {
        return ResponseEntity.ok(this.blogService.getBlogsByPagination(spec, pageable));
    }


    @GetMapping("/blogs/{id}")
    public ResponseEntity<BlogDTO> getBlogById(@PathVariable("id") String id) throws DataNotFoundException {

        Blog blog = this.blogService.getBlogById(id);

        if (blog == null) {
            throw new DataNotFoundException("Data not found");
        }

        return ResponseEntity.ok(this.blogService.coverBlogToBlogDTO(blog));
    }


    @ApiMessage("Created Successfully")
    @PostMapping("/blogs")
    public ResponseEntity<BlogDTO> createBlog(
            @RequestParam(name="file", required=false) MultipartFile file,
            @RequestParam(name="title") String title,
            @RequestParam(name="content") String content,
            @RequestParam(name="description") String description,
            @RequestParam(name="keyword") String keyword
    )
            throws URISyntaxException, IOException {

        Blog newBlog = new Blog();
        newBlog.setTitle(title);
        newBlog.setContent(content);
        newBlog.setDescription(description);
        newBlog.setKeyword(keyword);
        Blog blogCreated = this.blogService.createBlog(file, newBlog);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.blogService.coverBlogToBlogDTO(blogCreated));
    }

    @ApiMessage("Update Successfully")
    @PutMapping("/blogs")
    public ResponseEntity<BlogDTO> updateBlog(
            @RequestParam(name="file", required=false) MultipartFile file,
            @RequestParam(name="id") String id,
            @RequestParam(name="title") String title,
            @RequestParam(name="content") String content,
            @RequestParam(name="description") String description,
            @RequestParam(name="keyword") String keyword,
            @RequestParam(name="image") String image
    ) throws URISyntaxException, IOException, DataNotFoundException {

        Blog newBlog = new Blog();
        newBlog.setId(id);
        newBlog.setTitle(title);
        newBlog.setContent(content);
        newBlog.setDescription(description);
        newBlog.setKeyword(keyword);
        newBlog.setImage(image);

        Blog blogUpdated = this.blogService.updateBlog(file, newBlog);
        if (blogUpdated == null) {
            throw new DataNotFoundException("Data not found");
        }

        return ResponseEntity.ok(this.blogService.coverBlogToBlogDTO(blogUpdated));
    }

    @ApiMessage("Deleted Successfully")
    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<BlogDTO> deleteBlog(@PathVariable("id") String id) throws DataNotFoundException {
        Blog blog = this.blogService.getBlogById(id);
        if (blog == null) {
            throw new DataNotFoundException("Data not found");
        }
        this.blogService.deleteBlog(id);
        return ResponseEntity.ok(this.blogService.coverBlogToBlogDTO(blog));
    }

    @ApiMessage("Call api Successfully")
    @GetMapping("/blogs/carousel")
    public ResponseEntity<List<BlogDTO>> getCarouselBlogs() {
        return ResponseEntity.ok(this.blogService.getCarouselBlogs());
    }

    @ApiMessage("Call api Successfully")
    @GetMapping("/blogs/recent")
    public ResponseEntity<List<BlogDTO>> getRecentBlogs() {
        return ResponseEntity.ok(this.blogService.getRecentBlogs());
    }
}