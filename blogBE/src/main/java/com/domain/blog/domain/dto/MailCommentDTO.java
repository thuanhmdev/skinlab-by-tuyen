package com.domain.blog.domain.dto;

import com.domain.blog.domain.Blog;
import com.domain.blog.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MailCommentDTO {
    private String userName;
    private String commenterName;
    private String content;
    private Blog blog;
    private String blogUrl;
    private String siteName;

}
