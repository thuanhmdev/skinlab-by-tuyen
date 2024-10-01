package com.domain.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://skinlabbytuyen.io.vn", "http://skinlabbytuyen.io.vn", "http://103.167.89.154", "http://localhost:3000", "http://host.docker.internal", "http://localhost"));
//        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "x-no-retry"));
        // configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L); // set time pre-flight request cache (seconds unit)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // config cors for all api
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
