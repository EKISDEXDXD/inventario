package com.inventario.licoreria.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret:mi-clave-secreta-para-desarrollo-de-licoreria-2026}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(jwtExpiration);

        return Jwts.builder()
                .subject(username)
                .claim("role", "USER")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateTokenWithRole(String username, String role) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(jwtExpiration);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Object role = claims.get("role", Object.class);
            return role != null ? role.toString() : "USER";
        } catch (Exception e) {
            e.printStackTrace();
            return "USER";
        }
    }

    public boolean isTokenValid(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
