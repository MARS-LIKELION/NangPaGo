package com.mars.NangPaGo.auth.handler;

import com.mars.NangPaGo.domain.auth.service.TokenService;
import com.mars.NangPaGo.common.util.JwtUtil;
import com.mars.NangPaGo.auth.vo.CustomOAuth2User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${client.host}")
    private String clientHost;

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException {
        String email = ((CustomOAuth2User) authentication.getPrincipal()).getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String role = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("사용자 권한이 설정되지 않았습니다."));

        String access = jwtUtil.createJwt("access", email, role, jwtUtil.getAccessTokenExpireMillis());
        String refresh = jwtUtil.createJwt("refresh", email, role, jwtUtil.getRefreshTokenExpireMillis());

        tokenService.renewRefreshToken(email, refresh);

        response.addCookie(createCookie("access", access, jwtUtil.getAccessTokenExpireMillis(), false));
        response.addCookie(createCookie("refresh", refresh, jwtUtil.getRefreshTokenExpireMillis(), false));
        response.sendRedirect(clientHost);
    }

    private Cookie createCookie(String key, String value, long expireMillis, boolean httpOnly) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) (expireMillis / 1000));
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }
}
