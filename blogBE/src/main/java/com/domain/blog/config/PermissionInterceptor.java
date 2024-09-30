package com.domain.blog.config;


import com.domain.blog.domain.Permission;
import com.domain.blog.service.UserService;
import com.domain.blog.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

public class
PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;



    @Override
    @Transactional
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        System.out.println(">> path= " + path);
        System.out.println(">> requestURI= " + requestURI);
        System.out.println(">> httpMethod= " + httpMethod);

        String username = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if(username.isEmpty()){
            throw new BadCredentialsException("You do not have permission to access this endpoint");
        }

        List<Permission> permissions = this.userService.getPermissionFromUserRole(username);

        boolean isAllow =  permissions.stream().anyMatch(item -> item.getMethod().equals(httpMethod) && item.getApiPath().equals(path));
        if(!isAllow){
            throw new BadCredentialsException("You do not have permission to access this endpoint");
        }
        return true;
    }
}
