package com.domain.blog.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public final class FileConstant {
    public static List<String> allowedFileExtension = Arrays.asList("jpg", "jpeg", "png", "gif", "avif", "svg");
    public static String baseURI;
    public FileConstant(
            @Value("${app.upload-file.base-uri}") String baseURI
    ) {
        FileConstant.baseURI = baseURI;
    }
}