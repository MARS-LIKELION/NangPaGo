package com.mars.admin.domain.auth.service;

import static com.mars.common.exception.NPGExceptionType.BAD_REQUEST_INVALID;
import static com.mars.common.exception.NPGExceptionType.UNAUTHORIZED_TOKEN_EXPIRED;

import com.mars.admin.domain.auth.repository.RefreshTokenRepository;
import com.mars.common.dto.auth.RefreshTokenDto;
import com.mars.common.util.web.CookieUtil;
import com.mars.common.util.web.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void reissueTokens(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.findCookieByName(request, CookieUtil.REFRESH_TOKEN_NAME);
        validateRefreshToken(refreshToken);

        boolean isExist = refreshTokenRepository.existsByRefreshToken(refreshToken);
        if (!isExist) {
            cookieUtil.invalidateCookie(response, CookieUtil.REFRESH_TOKEN_NAME);
            throw BAD_REQUEST_INVALID.of("유효하지 않은 Refresh Token 입니다.");
        }

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        Long userId = jwtUtil.getId(refreshToken);

        String newAccessToken = jwtUtil.createJwt(CookieUtil.ACCESS_TOKEN_NAME, userId, email, role,
            jwtUtil.getAccessTokenExpireMillis());
        cookieUtil.addCookie(response, CookieUtil.ACCESS_TOKEN_NAME, newAccessToken,
            jwtUtil.getAccessTokenExpireMillis(), false);
    }

    @Transactional
    public void renewRefreshToken(String email, String refreshToken) {
        LocalDateTime expiration = LocalDateTime.now().plusNanos(jwtUtil.getRefreshTokenExpireMillis() * 1_000_000);
        refreshTokenRepository.deleteByEmail(email);
        refreshTokenRepository.save(new RefreshTokenDto(email, refreshToken, expiration).toEntity());
    }

    private void validateRefreshToken(String refreshToken) {
        if (Boolean.TRUE.equals(jwtUtil.isExpired(refreshToken))) {
            throw UNAUTHORIZED_TOKEN_EXPIRED.of("Refresh Token이 만료되었습니다.");
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            throw BAD_REQUEST_INVALID.of("유효하지 않은 Refresh Token입니다.");
        }
    }
}
