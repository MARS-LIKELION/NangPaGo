package com.mars.NangPaGo.domain.user.util;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null && processToken(token, request, response)) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean processToken(String token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (jwtUtil.validateToken(token)) {
                setAuthentication(token);
                return true;
            }
        } catch (ExpiredJwtException e) {
            return handleExpiredToken(request, response, e);
        }
        return false;
    }

    private void setAuthentication(String token) {
        Authentication authentication = jwtUtil.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("사용자 인증 성공: {}", authentication.getName());
    }

    private boolean handleExpiredToken(HttpServletRequest request, HttpServletResponse response, ExpiredJwtException e)
            throws IOException {
        log.warn("JWT 토큰 만료됨. 리프레시 토큰 필요.");
        String refreshToken = getRefreshTokenFromRequest(request);

        if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
            String email = e.getClaims().get("email", String.class);
            String newAccessToken = jwtUtil.createAccessToken(email, "ROLE_USER", jwtUtil.getAccessTokenExpire());
            response.setHeader("Authorization", "Bearer " + newAccessToken);
            log.info("새로운 액세스 토큰 발급 완료: {}", newAccessToken);
            return true;
        } else {
            log.error("리프레시 토큰 없음 또는 유효하지 않음. 요청 거부.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return false;
        }
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("RefreshToken".equals(cookie.getName())) {
                    log.info("리프레시 토큰 발견: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        log.info("리프레시 토큰이 요청에 없습니다.");
        return null;
    }

    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    log.info("Authorization 쿠키에서 JWT 추출 성공");
                    return cookie.getValue();
                }
            }
        }
        log.info("Authorization 쿠키가 없습니다.");
        return null;
    }
}
