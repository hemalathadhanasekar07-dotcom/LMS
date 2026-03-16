package com.project.lms.security;

import com.project.lms.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("JWT Service initialized successfully");
    }

    public String generateToken(User user) {
        log.info("Generating JWT token for user: {} with role: {}",
                user.getEmail(), user.getRole().getName());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        log.debug("Extracting claims from JWT token");

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        String username = extractAllClaims(token).getSubject();
        log.debug("Extracted username from token: {}", username);
        return username;
    }

    public String extractRole(String token) {
        String role = extractAllClaims(token).get("role", String.class);
        log.debug("Extracted role from token: {}", role);
        return role;
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            log.debug("JWT token is valid");
            return true;
        } catch (Exception ex) {
            log.warn("Invalid JWT token detected: {}", ex.getMessage());
            return false;
        }
    }
}