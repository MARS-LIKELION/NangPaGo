package com.mars.NangPaGo.domain.user.auth;

import com.mars.NangPaGo.domain.jwt.dto.RefreshTokenDto;
import com.mars.NangPaGo.domain.jwt.repository.RefreshTokenRepository;
import com.mars.NangPaGo.domain.jwt.util.JwtUtil;
import com.mars.NangPaGo.domain.user.vo.CustomOAuth2User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

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

        saveRefreshToken(email, refresh);

        response.addCookie(createCookie("access", access, jwtUtil.getAccessTokenExpireMillis()));
        response.addCookie(createCookie("refresh", refresh, jwtUtil.getRefreshTokenExpireMillis()));
        response.sendRedirect("http://localhost:5173/");
    }

    private void saveRefreshToken(String email, String refreshToken) {
        LocalDateTime expiration = LocalDateTime.now().plusNanos(jwtUtil.getRefreshTokenExpireMillis() * 1_000_000);
        refreshTokenRepository.deleteByRefreshToken(email);
        refreshTokenRepository.save(new RefreshTokenDto(email, refreshToken, expiration).toEntity());
    }

    private Cookie createCookie(String key, String value, long expireMillis) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) (expireMillis / 1000));
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
