package com.domain.blog.config;

import com.domain.blog.domain.dto.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();
    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;

    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseDTO<Object> responseDTO = new ResponseDTO<Object>();
        int statusCode = response.getStatus();
        if (statusCode == 400) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setMessage("Error...");
            responseDTO.setError("Bad request");
        } else if (statusCode == 404) {
            // Xử lý các trường hợp lỗi khác
            responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setError("404 NOT FOUND");
            responseDTO.setMessage("An unexpected error occurred");
        } else if (statusCode == 500) {
            // Xử lý các trường hợp lỗi khác
            responseDTO.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setError("INTERNAL SERVER ERROR");
            responseDTO.setMessage("An unexpected error occurred");
        } else {
            responseDTO.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            responseDTO.setMessage("Token is invalid (expired, not in the correct format, or does not transmit JWT in the header)...");
            responseDTO.setError(authException.getMessage());
        }

        response.setStatus(responseDTO.getStatusCode());
        response.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(response.getWriter(), responseDTO);
    }
}