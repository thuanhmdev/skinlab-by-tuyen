package com.domain.blog.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogDTO {
    private String id;
    private String title;
    private String description;
    private String keyword;
    private String image;
    private String content;
    private UserDTO blogger;
    private Long createdAt;
    private Long updatedAt;
}