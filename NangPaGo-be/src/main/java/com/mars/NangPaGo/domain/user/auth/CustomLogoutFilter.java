package com.mars.NangPaGo.domain.user.auth;

import com.mars.NangPaGo.domain.user.service.CustomLogoutService;
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

    private final CustomLogoutService customLogoutService;

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
            String refreshToken = extractRefreshToken(httpRequest);
            customLogoutService.handleLogout(refreshToken, httpResponse);
        } catch (IllegalArgumentException e) {
            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpResponse.getWriter().write(e.getMessage());
        }
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return "/logout".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod());
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
}
