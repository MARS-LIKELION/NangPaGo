package com.mars.NangPaGo.domain.jwt.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpireMillis;
    private final long refreshTokenExpireMillis;

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.token.access-expiration-time}") Long accessTokenExpireMillis,
        @Value("${jwt.token.refresh-expiration-time}") Long refreshTokenExpireMillis
    ) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SIG.HS256.key().build().getAlgorithm());
        this.accessTokenExpireMillis = accessTokenExpireMillis;
        this.refreshTokenExpireMillis = refreshTokenExpireMillis;
    }

    public String getCategory(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("category", String.class);
    }

    public String getEmail(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("email", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration()
            .before(new Date());
    }

    public String createJwt(String category, String email, String role, Long expiredMs) {
        return Jwts.builder()
            .claim("category", category)
            .claim("email", email)
            .claim("role", role)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact();
    }

    public long getAccessTokenExpireMillis() {
        return accessTokenExpireMillis;
    }

    public long getRefreshTokenExpireMillis() {
        return refreshTokenExpireMillis;
    }
}
