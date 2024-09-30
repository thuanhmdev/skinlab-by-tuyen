package com.domain.blog.domain;
import com.domain.blog.constant.ProviderEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import com.domain.blog.constant.RoleEnum;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message="Username ís required")
    private String username;

    @NotBlank(message="Password ís required")
    private String password;

    private String image;
    private String imageProvider;

    @NotBlank(message="Name ís required")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private ProviderEnum provider;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "blogger")
    @JsonIgnore
    private List<Blog> blogs;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comments;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id")
    private Role role;
}