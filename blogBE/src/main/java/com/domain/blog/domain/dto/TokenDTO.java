package com.domain.blog.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {
    String refreshToken;
    String accessToken;
}
