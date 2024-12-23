package com.mars.NangPaGo.domain.user.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessTokenExpire;
    private final Long refreshTokenExpire;

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.token.access-expiration-time}") Long access,
        @Value("${jwt.token.refresh-expiration-time}") Long refresh
    ) {
        this.secretKey = new SecretKeySpec(secret.getBytes(UTF_8), SIG.HS256.key().build().getAlgorithm());
        this.accessTokenExpire = access;
        this.refreshTokenExpire = refresh;
    }

    public String createAccessToken(String email, String role, Long expiredMs) {
        return Jwts.builder()
            .claim("email", email)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact();
    }

    public String createRefreshToken(String email, Long expiredMs) {
        return Jwts.builder()
            .claim("email", email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        Collection<GrantedAuthority> authorities =
            Collections.singletonList(new SimpleGrantedAuthority(role));

        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }

    public Long getAccessTokenExpire() {
        return accessTokenExpire;
    }

    public Long getRefreshTokenExpire() {
        return refreshTokenExpire;
    }
}

