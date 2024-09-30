package com.domain.blog.domain.dto;

import com.domain.blog.constant.ProviderEnum;
import com.domain.blog.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String name;
    private String image;
    private String imageProvider;
    private RoleUser role;
    private ProviderEnum provider;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleUser {
        private String id;
        private String name;
    }
}