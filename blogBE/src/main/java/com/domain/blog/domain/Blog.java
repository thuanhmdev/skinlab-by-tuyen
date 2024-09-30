package com.domain.blog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;


@Entity(name="blogs")
@Getter
@Setter
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String keyword;
    private String image;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message="Content is required")
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="blogger_id")
    private User blogger;

    @OneToMany(mappedBy = "blog", fetch = FetchType.LAZY, cascade=CascadeType.REMOVE , orphanRemoval = true)
    @JsonIgnoreProperties(value ={"user"})
    private List<Comment> comments;

    private Long createdAt;
    private Long updatedAt;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now().toEpochMilli();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now().toEpochMilli();
    }

}
