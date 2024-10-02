package com.domain.blog.domain.dto;

import com.domain.blog.domain.Blog;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {
    private String id;
    private String content;
    private Long createdAt;
    private String rootId;
    private UserDTO user;
    private UserDTO replyToUser;
    private Blog blog;
    private String blogUrl;
}
