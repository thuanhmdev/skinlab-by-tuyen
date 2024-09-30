package com.domain.blog.config;

import com.domain.blog.annotation.ApiMessage;
import com.domain.blog.domain.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class FormatResponse implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int statusCode = servletResponse.getStatus();

        if (body instanceof Resource || body instanceof String || statusCode >= 400) {
            return body;
        }
        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }


        ResponseDTO<Object> responseDTO = new ResponseDTO<Object>();
        responseDTO.setStatusCode(statusCode);
        responseDTO.setData(body);
        ApiMessage apiMessage = returnType.getMethodAnnotation(ApiMessage.class);
        responseDTO.setMessage(apiMessage != null ? apiMessage.value() : "Call API Success");
        return responseDTO;

    }
}
