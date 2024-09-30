package com.domain.blog.constant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;


public final class SecurityConstant {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
}