package com.domain.blog.domain.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO<T> {
    private String error;
    private Object message;
    private T data;
    private int statusCode;
}
