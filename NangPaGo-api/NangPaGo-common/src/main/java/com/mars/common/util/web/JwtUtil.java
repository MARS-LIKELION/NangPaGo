package com.mars.common.util.web;

import com.mars.common.auth.UserDetailsImpl;
import com.mars.common.exception.NPGExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

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

    public Long getId(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("id", Long.class);
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
        try {
            Date expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public String createJwt(String category, Long userId, String email, String role, Long expiredMs) {
        return Jwts.builder()
            .claim("category", category)
            .claim("id", userId)
            .claim("email", email)
            .claim("role", role)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        
        Collection<? extends GrantedAuthority> authorities = 
            Arrays.stream(new String[]{claims.get("role").toString()})
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        UserDetailsImpl principal = UserDetailsImpl.builder()
            .id(claims.get("id", Long.class))
            .email(claims.get("email").toString())
            .authorities(authorities)
            .build();
        
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public String getAccessTokenFrom(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw NPGExceptionType.UNAUTHORIZED_TOKEN_EXPIRED.of("인증 토큰이 없습니다.");
        }

        String accessToken = Arrays.stream(cookies)
            .filter(cookie -> "access".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElseThrow(() -> NPGExceptionType.UNAUTHORIZED_TOKEN_EXPIRED.of("인증 토큰이 없습니다."));

        if (Boolean.TRUE.equals(isExpired(accessToken))) {
            throw NPGExceptionType.UNAUTHORIZED_TOKEN_EXPIRED.of("만료된 토큰입니다.");
        }

        return accessToken;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public long getAccessTokenExpireMillis() {
        return accessTokenExpireMillis;
    }

    public long getRefreshTokenExpireMillis() {
        return refreshTokenExpireMillis;
    }
}
