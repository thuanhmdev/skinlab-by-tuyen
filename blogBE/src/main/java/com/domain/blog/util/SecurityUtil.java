package com.domain.blog.util;

import com.domain.blog.domain.dto.UserDTO;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.domain.blog.constant.SecurityConstant.JWT_ALGORITHM;

public class SecurityUtil {

//    @Value("${app.jwt.jwt-secret-key}")
//    private static String jwtSecretKey;

    public static String createToken(UserDTO userDTO,  JwtEncoder jwtEncoder, long jwtExpiration) {
        Instant now = Instant.now();
        Instant validity = now.plus(jwtExpiration, ChronoUnit.SECONDS);

        // header
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        //List Authority
        List<String> authorities = new ArrayList<String>();
        authorities.add("ROLE_ADMIN");

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder().issuedAt(now).expiresAt(validity)
                .subject(userDTO.getUsername())
                .claim("user", userDTO)
                .claim("permission", authorities)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public static String createRefreshToken(String username, UserDTO userDTO, JwtEncoder jwtEncoder, long refreshTokenExpiration ) {
        Instant now = Instant.now();
        Instant validity = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS);

        // header
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder().issuedAt(now).expiresAt(validity)
                .subject(username)
                .claim("user", userDTO)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    private static SecretKey getSecretKey(String jwtSecretKey) {
        byte[] keyBytes = Base64.from(jwtSecretKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                JWT_ALGORITHM.getName());
    }

    public static Jwt checkValidRefreshToken(String token, String jwtSecretKey){
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey(jwtSecretKey)).macAlgorithm(JWT_ALGORITHM).build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            System.out.println(">>> Refresh Token error: " + e.getMessage());
            throw e;

        }
    }
}