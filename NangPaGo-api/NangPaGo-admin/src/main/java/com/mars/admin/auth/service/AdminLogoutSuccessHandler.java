package com.mars.admin.auth.service;

import com.mars.admin.domain.auth.repository.RefreshTokenRepository;
import com.mars.common.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {

        try {
            String refreshToken = extractRefreshToken(request);
            handleLogout(refreshToken, response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(e.getMessage());
        }
    }


    private void handleLogout(String refreshToken, HttpServletResponse response) throws IOException {
        validateRefreshToken(refreshToken);

        if (Boolean.FALSE.equals(refreshTokenRepository.existsByRefreshToken(refreshToken))) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtUtil.getEmail(refreshToken);
        refreshTokenRepository.deleteByEmail(email);

        invalidateCookie(response, "refresh");
        invalidateCookie(response, "access");

        response.setStatus(HttpStatus.OK.value());
        response.getWriter().flush();
    }

    private void validateRefreshToken(String refreshToken) {
        try {
            if (Boolean.TRUE.equals(jwtUtil.isExpired(refreshToken))) {
                throw new IllegalArgumentException("Refresh Token이 만료되었습니다.");
            }

            if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
                throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
            }
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다.");
        }
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new IllegalArgumentException("요청에 쿠키가 존재하지 않습니다.");
        }

        return Arrays.stream(cookies)
            .filter(cookie -> "refresh".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Refresh Token이 존재하지 않습니다."));
    }

    private void invalidateCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
