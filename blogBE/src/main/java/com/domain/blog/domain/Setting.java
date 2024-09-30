package com.domain.blog.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="settings")
public class Setting {
    @Id
    private String id;
    private String title;
    private String siteName;
    private String email;
    private String description;
    private String facebookLink;
    private String messengerLink;
    private String xLink;
    private String instagramLink;
    private String fanpageFacebookLink;
}
