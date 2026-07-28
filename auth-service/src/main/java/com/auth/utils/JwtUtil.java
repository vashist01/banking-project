package com.auth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;

@Component
@Slf4j
public class JwtUtil {
    private static final String SECRET =
            "9f4c2e7b3d8a1f6c5b9e0a2d4c7f8b1e6d3a9c2e5f7b4a1d8c6e3f0b2a9d7c5";
    private static final SecretKey secretKey =Keys.hmacShaKeyFor(SECRET.getBytes());

    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    public String generateToken(String email, Set<String> roles, Long id) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime()+expirationMs);
        return Jwts.builder().setSubject(email).claim("roles",roles).claim("userId",id)
                .setIssuedAt(now).setExpiration(expiryDate).signWith(secretKey).compact();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        }catch (Exception exception){
            log.error("JWT validation failed: {}", exception.getMessage());
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

    }

    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId").toString();
    }

    public Set<String> getUserRoles(String token) {
        Claims claims = extractAllClaims(token);
        String roles = claims.get("roles").toString();
        return Set.of(roles);
    }
}
