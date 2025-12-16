package com.example.notificationservice.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Jws<Claims> validateAndGetClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
    }
}
