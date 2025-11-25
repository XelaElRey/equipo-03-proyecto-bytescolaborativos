package com.equipo03.motorRecomendaciones.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Long expiration;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication auth) {
        String username = auth.getName();
        String authority = auth.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Usuario sin rol asignado"))
                .getAuthority();

        String roleWithoutPrefix = authority.startsWith("ROLE_")
                ? authority.substring(5)
                : authority;

        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", roleWithoutPrefix)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromJwt(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("ERROR VALIDANDO TOKEN: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return false;
        }

    }

    public String getSecret() {
        return secret;
    }
}