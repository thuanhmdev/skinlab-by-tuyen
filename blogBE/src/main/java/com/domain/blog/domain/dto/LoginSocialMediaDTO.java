package com.domain.blog.domain.dto;

import com.domain.blog.constant.ProviderEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginSocialMediaDTO {

    private ProviderEnum provider;
    private String username;
    private String name;
    private String imageProvider;
}
