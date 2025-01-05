package com.mars.NangPaGo.domain.jwt.service;

import com.mars.NangPaGo.domain.jwt.dto.RefreshTokenDto;
import com.mars.NangPaGo.domain.jwt.repository.RefreshTokenRepository;
import com.mars.NangPaGo.domain.jwt.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.*;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void reissueTokens(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromRequest(request);
        validateRefreshToken(refreshToken);

        boolean isExist = refreshTokenRepository.existsByRefreshToken(refreshToken);
        if (!isExist) {
            throw BAD_REQUEST_INVALID.of("유효하지 않은 Refresh Token 입니다.");
        }

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", email, role, jwtUtil.getAccessTokenExpireMillis());

        response.addCookie(createCookie("access", newAccessToken, jwtUtil.getAccessTokenExpireMillis()));
    }

    @Transactional
    public void renewRefreshToken(String email, String refreshToken) {
        LocalDateTime expiration = LocalDateTime.now().plusNanos(jwtUtil.getRefreshTokenExpireMillis() * 1_000_000);
        refreshTokenRepository.deleteByEmail(email);
        refreshTokenRepository.save(new RefreshTokenDto(email, refreshToken, expiration).toEntity());
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw BAD_REQUEST_INVALID.of("요청에 쿠키가 존재하지 않습니다.");
        }
        return Arrays.stream(cookies)
            .filter(cookie -> "refresh".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElseThrow(() -> BAD_REQUEST_INVALID.of("Refresh Token이 존재하지 않습니다."));
    }

    private void validateRefreshToken(String refreshToken) {
        if (Boolean.TRUE.equals(jwtUtil.isExpired(refreshToken))) {
            throw UNAUTHORIZED_TOKEN_EXPIRED.of("Refresh Token이 만료되었습니다.");
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            throw BAD_REQUEST_INVALID.of("유효하지 않은 Refresh Token입니다.");
        }
    }

    private Cookie createCookie(String key, String value, long expireMillis) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) (expireMillis / 1000));
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        return cookie;
    }
}
