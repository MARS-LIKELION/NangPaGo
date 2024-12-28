package com.mars.NangPaGo.domain.user.service;

import com.mars.NangPaGo.domain.jwt.repository.RefreshTokenRepository;
import com.mars.NangPaGo.domain.jwt.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomLogoutService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void handleLogout(String refreshToken, HttpServletResponse response) {
        validateRefreshToken(refreshToken);

        if (!refreshTokenRepository.existsByRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        refreshTokenRepository.deleteByRefreshToken(refreshToken);

        invalidateCookie(response, "refresh");
        invalidateCookie(response, "access");

        response.setStatus(HttpStatus.OK.value());
    }

    private void validateRefreshToken(String refreshToken) {
        try {
            if (jwtUtil.isExpired(refreshToken)) {
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
