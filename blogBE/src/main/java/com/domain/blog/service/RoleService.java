package com.domain.blog.service;

import com.domain.blog.domain.Permission;
import com.domain.blog.domain.Role;
import com.domain.blog.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service("RoleService")
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public RoleService(RoleRepository roleRepository, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
    }

    @PostConstruct
    public void init() {
        if(roleRepository.count() == 0) {
            createAdminRole();
            createUserRole();
        }
    }


    public void createAdminRole() {
        Role adminRole = new Role();
        adminRole.setId("ADMIN");
        adminRole.setName("ADMIN");
        adminRole.setActive(true);
        List<Permission> allPermissions = this.permissionService.getPermissions();
        adminRole.setPermissions(allPermissions);
        roleRepository.save(adminRole);
    }

    public void createUserRole() {
        Role userRole = new Role();
        userRole.setId("USER");
        userRole.setName("USER");
        userRole.setActive(true);
        List<Permission> allPermissions = this.permissionService.getPermissions();
        userRole.setPermissions(Arrays.asList(
                this.permissionService.createPermission("GET_ACCOUNT", "GET_ACCOUNT", "/blog-api/admin/auth/account", "GET"),
                this.permissionService.createPermission("REFRESH_TOKEN", "REFRESH_TOKEN", "/blog-api/auth/refresh", "POST"),
                this.permissionService.createPermission("LOGOUT", "LOGOUT", "/blog-api/auth/logout", "POST"),
                this.permissionService.createPermission("LOGIN_SOCIAL_MEDIA", "LOGIN_SOCIAL_MEDIA", "/blog-api/auth/social-media", "POST"),

                this.permissionService.createPermission("GET_ALL_BLOGS", "GET_ALL_BLOGS", "/blog-api/blogs", "GET"),
                this.permissionService.createPermission("GET_BLOGS_PAGINATION", "GET_BLOGS_PAGINATION", "/blog-api/blogs-pagination", "GET"),
                this.permissionService.createPermission("GET_BLOG_BY_ID", "GET_BLOG_BY_ID", "/blog-api/blogs/{id}", "GET"),
                this.permissionService.createPermission("GET_CAROUSEL_BLOGS", "GET_CAROUSEL_BLOGS", "/blog-api/blogs/carousel", "GET"),
                this.permissionService.createPermission("GET_RECENT_BLOGS", "GET_RECENT_BLOGS", "/blog-api/blogs/recent", "GET"),

                this.permissionService.createPermission("GET_ALL_COMMENTS", "GET_ALL_COMMENTS", "/blog-api/blogs/comments", "GET"),
                this.permissionService.createPermission("GET_COMMENTS_BY_BLOG_ID", "GET_COMMENTS_BY_BLOG_ID", "/blog-api/blogs/comments/{blogId}", "GET"),
                this.permissionService.createPermission("CREATE_COMMENT", "CREATE_COMMENT", "/blog-api/blogs/comments", "POST"),

                this.permissionService.createPermission("GET_SETTING", "GET_SETTING", "/blog-api/settings", "GET"),
                this.permissionService.createPermission("GET_CONTACT_ADMIN_INFO", "GET_CONTACT_ADMIN_INFO", "/blog-api/settings/contact", "GET")

        ));
        roleRepository.save(userRole);
    }

    public Role findByName(String name) {
        return this.roleRepository.findByName(name);
    }
}
