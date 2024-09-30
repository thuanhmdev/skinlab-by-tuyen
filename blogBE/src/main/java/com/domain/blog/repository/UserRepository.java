package com.domain.blog.repository;

import com.domain.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    User findByRefreshTokenAndUsername(String refreshToken, String username);
}
