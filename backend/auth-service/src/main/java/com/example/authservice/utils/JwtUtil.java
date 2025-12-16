package com.example.authservice.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    private String jwtSecret = "MySecretKeyForEventApp1234567890MySecretKeyForEventApp1234567890";

    // hardcoded for simplicity in demo or read from props
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // override with prop if needed, or stick to prop reading
    @Value("${jwt.secret}")
    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public String generateToken(String userId, String role) {
        return Jwts.builder()
                .claim("id", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public Jws<Claims> validateAndGetClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
    }
}
