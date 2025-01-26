package com.mars.admin.auth.service;

import com.mars.admin.domain.auth.repository.RefreshTokenRepository;
import com.mars.common.exception.NPGExceptionType;
import com.mars.common.util.web.CookieUtil;
import com.mars.common.util.web.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final CookieUtil cookieUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException {

        String refreshToken = cookieUtil.findCookieByName(request, CookieUtil.REFRESH_TOKEN_NAME);
        handleLogout(refreshToken, response);
    }


    private void handleLogout(String refreshToken, HttpServletResponse response) throws IOException {
        validateRefreshToken(refreshToken);

        if (Boolean.FALSE.equals(refreshTokenRepository.existsByRefreshToken(refreshToken))) {
            throw NPGExceptionType.UNAUTHORIZED_TOKEN_EXPIRED.of("유효하지 않은 Refresh Token입니다.");
        }

        String email = jwtUtil.getEmail(refreshToken);
        refreshTokenRepository.deleteByEmail(email);

        cookieUtil.invalidateCookie(response, CookieUtil.REFRESH_TOKEN_NAME);
        cookieUtil.invalidateCookie(response, CookieUtil.ACCESS_TOKEN_NAME);

        response.setStatus(HttpStatus.OK.value());
        response.getWriter().flush();
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
