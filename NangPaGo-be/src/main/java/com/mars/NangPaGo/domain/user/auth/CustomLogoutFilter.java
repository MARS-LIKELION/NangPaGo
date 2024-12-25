package com.mars.NangPaGo.domain.user.auth;

import com.mars.NangPaGo.domain.user.repository.RefreshTokenRepository;
import com.mars.NangPaGo.domain.user.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
@Component
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!isLogoutRequest(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            handleLogout(httpRequest, httpResponse);
        } catch (IllegalArgumentException e) {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpResponse.getWriter().write(e.getMessage());
        }
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return "/logout".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod());
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);

        validateRefreshToken(refreshToken);

        if (!refreshTokenRepository.existsByRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        refreshTokenRepository.deleteByRefreshToken(refreshToken);

        // Refresh Token 쿠키 삭제
        invalidateCookie(response, "refresh");

        // Access Token 쿠키 삭제
        invalidateCookie(response, "access");

        // 사용자 세션 초기화 (필요한 경우)
        request.getSession().invalidate();

        response.setStatus(HttpStatus.OK.value());
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
        Cookie cookie = new Cookie(cookieName, null); // 값 제거
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setPath("/"); // 전체 경로에서 삭제
        response.addCookie(cookie);
    }
}
