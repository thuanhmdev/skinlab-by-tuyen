package com.domain.blog.domain.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CommentDTO {
    private String id;
    private String content;
    private Long createdAt;
    private UserDTO user;
    private String blogId;
    private String rootId;
}
