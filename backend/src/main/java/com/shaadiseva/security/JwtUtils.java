package com.shaadiseva.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    private final SecretKey key;
    private final long expiryMs;
    private final long refreshExpiryMs;

    public JwtUtils(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiry-ms}") long expiryMs,
            @Value("${app.jwt.refresh-expiry-ms}") long refreshExpiryMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiryMs = expiryMs;
        this.refreshExpiryMs = refreshExpiryMs;
    }

    public String generateToken(UserDetails userDetails, boolean isRefresh) {
        long now = System.currentTimeMillis();
        long expiry = isRefresh ? refreshExpiryMs : expiryMs;
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("CUSTOMER");

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", role)
                .claim("refresh", isRefresh)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiry))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
