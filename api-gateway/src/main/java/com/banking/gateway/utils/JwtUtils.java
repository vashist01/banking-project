package com.banking.gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtUtils {
    private static final String SECRET =
            "9f4c2e7b3d8a1f6c5b9e0a2d4c7f8b1e6d3a9c2e5f7b4a1d8c6e3f0b2a9d7c5";
    private static final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
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
        return Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getPayload();

    }


}
