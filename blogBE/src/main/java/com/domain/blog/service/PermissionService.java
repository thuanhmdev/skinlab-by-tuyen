package com.domain.blog.service;


import com.domain.blog.domain.Permission;
import com.domain.blog.repository.PermissionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service("PermissionService")
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    Permission createPermission(String id, String name, String apiPath, String method) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setName(name);
        permission.setApiPath(apiPath);
        permission.setMethod(method);
        permission.setActive(true);
        return permission;
    }

    @PostConstruct
    public void initializePermission() {
        if (permissionRepository.count() == 0) {
            List<Permission> permissions = Arrays.asList(
                    createPermission("READ_USER", "READ_USER", "/blog-api/users/{id}", "GET"),
                    createPermission("UPDATE_USER", "UPDATE_USER", "/blog-api/users", "PUT"),

                    createPermission("LOGIN_ADMIN", "LOGIN_ADMIN", "/blog-api/admin/auth/login", "POST"),
                    createPermission("GET_ACCOUNT", "GET_ACCOUNT", "/blog-api/admin/auth/account", "GET"),
                    createPermission("REFRESH_TOKEN", "REFRESH_TOKEN", "/blog-api/auth/refresh", "POST"),
                    createPermission("LOGOUT", "LOGOUT", "/blog-api/auth/logout", "POST"),
                    createPermission("LOGIN_SOCIAL_MEDIA", "LOGIN_SOCIAL_MEDIA", "/blog-api/auth/social-media", "POST"),

                    createPermission("GET_ALL_BLOGS", "GET_ALL_BLOGS", "/blog-api/blogs", "GET"),
                    createPermission("GET_BLOGS_PAGINATION", "GET_BLOGS_PAGINATION", "/blog-api/blogs-pagination", "GET"),
                    createPermission("GET_BLOG_BY_ID", "GET_BLOG_BY_ID", "/blog-api/blogs/{id}", "GET"),
                    createPermission("CREATE_BLOG", "CREATE_BLOG", "/blog-api/blogs", "POST"),
                    createPermission("UPDATE_BLOG", "UPDATE_BLOG", "/blog-api/blogs", "PUT"),
                    createPermission("DELETE_BLOG", "DELETE_BLOG", "/blog-api/blogs/{id}", "DELETE"),
                    createPermission("GET_CAROUSEL_BLOGS", "GET_CAROUSEL_BLOGS", "/blog-api/blogs/carousel", "GET"),
                    createPermission("GET_RECENT_BLOGS", "GET_RECENT_BLOGS", "/blog-api/blogs/recent", "GET"),

                    createPermission("GET_ALL_COMMENTS", "GET_ALL_COMMENTS", "/blog-api/blogs/comments", "GET"),
                    createPermission("GET_COMMENTS_BY_BLOG_ID", "GET_COMMENTS_BY_BLOG_ID", "/blog-api/blogs/comments/{blogId}", "GET"),
                    createPermission("CREATE_COMMENT", "CREATE_COMMENT", "/blog-api/blogs/comments", "POST"),
                    createPermission("DELETE_COMMENT", "DELETE_COMMENT", "/blog-api/blogs/comments/{id}", "DELETE"),

                    createPermission("GET_SETTING", "GET_SETTING", "/blog-api/settings", "GET"),
                    createPermission("GET_CONTACT_ADMIN_INFO", "GET_CONTACT_ADMIN_INFO", "/blog-api/settings/contact", "GET"),
                    createPermission("UPDATE_SETTING", "UPDATE_SETTING", "/blog-api/settings", "PUT")
            );

            permissionRepository.saveAll(permissions);
        }
    }

    public List<Permission> getPermissions() {
        return permissionRepository.findAll();
    }

}
