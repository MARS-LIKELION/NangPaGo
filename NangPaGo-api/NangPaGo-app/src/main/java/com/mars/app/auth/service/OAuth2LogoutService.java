package com.mars.app.auth.service;

import com.mars.common.exception.NPGExceptionType;
import com.mars.common.util.web.CookieUtil;
import com.mars.common.util.web.JwtUtil;
import com.mars.app.domain.auth.repository.RefreshTokenRepository;
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
    private final CookieUtil cookieUtil;

    @Transactional
    public void handleLogout(String refreshToken, HttpServletResponse response) {
        validateRefreshToken(refreshToken);

        if (Boolean.FALSE.equals(refreshTokenRepository.existsByRefreshToken(refreshToken))) {
            throw NPGExceptionType.UNAUTHORIZED_TOKEN_EXPIRED.of("유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtUtil.getEmail(refreshToken);
        refreshTokenRepository.deleteByEmail(email);

        cookieUtil.invalidateCookie(response, CookieUtil.REFRESH_TOKEN_NAME);
        cookieUtil.invalidateCookie(response, CookieUtil.ACCESS_TOKEN_NAME);

        response.setStatus(HttpStatus.OK.value());
    }

    private void validateRefreshToken(String refreshToken) {
        if (Boolean.TRUE.equals(jwtUtil.isExpired(refreshToken))) {
            throw NPGExceptionType.UNAUTHORIZED_TOKEN_EXPIRED.of("Refresh Token이 만료되었습니다.");
        }

        if (!CookieUtil.REFRESH_TOKEN_NAME.equals(jwtUtil.getCategory(refreshToken))) {
            throw NPGExceptionType.UNAUTHORIZED_TOKEN_EXPIRED.of("유효하지 않은 Refresh Token입니다.");
        }
    }
}
