package com.domain.blog.service;

import com.domain.blog.domain.Blog;
import com.domain.blog.domain.User;
import com.domain.blog.domain.dto.BlogDTO;
import com.domain.blog.domain.dto.ResultPaginationDTO;
import com.domain.blog.repository.BlogRepository;
import com.domain.blog.util.FileUtil;
import com.domain.blog.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.domain.blog.constant.FileConstant.allowedFileExtension;
import static com.domain.blog.constant.FileConstant.baseURI;

@Service
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserService userService;

    public BlogService(BlogRepository blogRepository, UserService userService) {
        this.blogRepository = blogRepository;
        this.userService = userService;
    }

    public BlogDTO coverBlogToBlogDTO(Blog blog) {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setId(blog.getId());
        blogDTO.setTitle(blog.getTitle());
        blogDTO.setContent(blog.getContent());
        blogDTO.setImage(blog.getImage());
        blogDTO.setCreatedAt(blog.getCreatedAt());
        blogDTO.setUpdatedAt(blog.getUpdatedAt());
        blogDTO.setDescription(blog.getDescription());
        blogDTO.setKeyword(blog.getKeyword());
        blogDTO.setBlogger(this.userService.convertUserToUserDTO(blog.getBlogger()));

        return blogDTO;
    }

    public List<BlogDTO> getBlogs() {
        List<Blog> blogs = this.blogRepository.findAll();
        return blogs.stream().map(this::coverBlogToBlogDTO).toList();
    }

    public ResultPaginationDTO getBlogsByPagination(Specification<Blog> specification, Pageable pageable) {
        Page<Blog> blogs = this.blogRepository.findAll(specification, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        //Meta
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setSize(pageable.getPageSize());
        meta.setPages(blogs.getTotalPages());
        meta.setTotal((int) blogs.getTotalElements());
        resultPaginationDTO.setMeta(meta);

        //Result
        resultPaginationDTO.setResult(blogs.getContent().stream().map(this::coverBlogToBlogDTO).toList());
        return resultPaginationDTO;

    }


    public Blog getBlogById(String id) {
        Optional<Blog> blogOptional = this.blogRepository.findById(id);
        return blogOptional.orElse(null);
    }

    public Blog createBlog(MultipartFile file, Blog blog) throws URISyntaxException, IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            boolean isValid = allowedFileExtension.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
            if (isValid) {
                //Create a directory if not Exist
                FileUtil.createDirectory(baseURI + "blog");
                //Store file and save name to DB
                String fileNameFinal = FileUtil.storeFile(file, "blog");
                blog.setImage(("/" +"blog" + "/" + fileNameFinal));
            }
        }
        String usernameBlogger = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;

        User blogger = this.userService.getUserByUsername(usernameBlogger);
        blog.setBlogger(blogger);

        return this.blogRepository.save(blog);
    }

    public Blog updateBlog(MultipartFile file, Blog blog) throws URISyntaxException, IOException {
        Blog blogUpdate = this.getBlogById(blog.getId());
        if (blogUpdate == null) {
            return null;
        }

        blogUpdate.setTitle(blog.getTitle());
        blogUpdate.setDescription(blog.getDescription());
        blogUpdate.setKeyword(blog.getKeyword());
        blogUpdate.setContent(blog.getContent());

        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            boolean isValid = allowedFileExtension.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
            if (isValid) {

                // Delete old file if exists
                if(blogUpdate.getImage() != null && !blogUpdate.getImage().isEmpty()){
                    FileUtil.deleteOldFile(fileName, "blog");
                }

                //Create a directory if not Exist
                FileUtil.createDirectory(baseURI + "blog");
                //Store file and save name to DB
                String fileNameFinal = FileUtil.storeFile(file, "blog");
                blogUpdate.setImage(("/" +"blog" + "/" + fileNameFinal));
            }
        } else {
            if (Objects.equals(blog.getImage(), "") || blog.getImage() == null) {
                // Delete old file if exists
                if(blogUpdate.getImage() != null && !blogUpdate.getImage().isEmpty()){
                    FileUtil.deleteOldFile(blogUpdate.getImage(), "blog");
                }
                blogUpdate.setImage(null);
            } else {
                blogUpdate.setImage(blogUpdate.getImage());
            }
        }
        return this.blogRepository.save(blogUpdate);
    }

    public void deleteBlog(String id) {
        this.blogRepository.deleteById(id);
    }

    public List<BlogDTO> getCarouselBlogs () {
        Page<Blog> page = this.blogRepository.findAll(PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt")));
        return page.getContent().stream().map(this::coverBlogToBlogDTO).toList();
    }

    public boolean existsById(String id) {
        return blogRepository.existsById(id);
    }

    public List<BlogDTO> getRecentBlogs() {
        return blogRepository.findTop10ByOrderByCreatedAtDesc().stream().map(this::coverBlogToBlogDTO).toList();
    }
}
