package com.business.api_gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Validate JWT Token
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Verify issuer
            if (!issuer.equals(claims.getIssuer())) {
                log.error("Invalid token issuer: {}", claims.getIssuer());
                return false;
            }

            // Check expiration
            if (claims.getExpiration().before(new Date())) {
                log.error("Token is expired");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Extract all claims
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract specific fields
    public String extractUserId(String token) {
        return extractClaims(token).get("userId", String.class);
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRoles(String token) {
        return extractClaims(token).get("roles", String.class);
    }

    public String extractJti(String token) {
        return extractClaims(token).getId();
    }

    public String extractTokenType(String token) {
        return extractClaims(token).get("tokenType", String.class);
    }
}
