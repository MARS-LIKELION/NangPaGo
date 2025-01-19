package com.mars.app.auth.service;

import com.mars.common.util.JwtUtil;
import com.mars.app.domain.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuth2LogoutService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void handleLogout(String refreshToken, HttpServletResponse response) {
        validateRefreshToken(refreshToken);

        if (Boolean.FALSE.equals(refreshTokenRepository.existsByRefreshToken(refreshToken))) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtUtil.getEmail(refreshToken);
        refreshTokenRepository.deleteByEmail(email);

        invalidateCookie(response, "refresh");
        invalidateCookie(response, "access");

        response.setStatus(HttpStatus.OK.value());
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

    private void invalidateCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
