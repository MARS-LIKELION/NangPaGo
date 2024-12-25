package com.mars.NangPaGo.domain.user.auth;

import com.mars.NangPaGo.domain.user.dto.RefreshTokenDto;
import com.mars.NangPaGo.domain.user.repository.RefreshTokenRepository;
import com.mars.NangPaGo.domain.user.util.JwtUtil;
import com.mars.NangPaGo.domain.user.vos.CustomOAuth2User;
import jakarta.servlet.ServletException;
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
        throws IOException, ServletException {
        String email = ((CustomOAuth2User) authentication.getPrincipal()).getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        log.info("authorities = {} ", authorities.toString());
        String role = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("사용자 권한이 설정되지 않았습니다."));
        log.info("인증 성공: 사용자 이메일 = {}, 역할 = {}", email, role);

        String access = jwtUtil.createJwt("access", email, role, jwtUtil.getAccessTokenExpireMillis());
        String refresh = jwtUtil.createJwt("refresh", email, role, jwtUtil.getRefreshTokenExpireMillis());

        saveRefreshToken(email, refresh);

        log.info("access = {}", access);
        log.info("refresh = {}", refresh);
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.sendRedirect("http://localhost:5173/");
    }

    private void saveRefreshToken(String email, String refreshToken) {
        LocalDateTime expiration = LocalDateTime.now().plusNanos(jwtUtil.getRefreshTokenExpireMillis() * 1_000_000);
        refreshTokenRepository.deleteByRefreshToken(email);
        refreshTokenRepository.save(new RefreshTokenDto(refreshToken, email, expiration).toEntity());
        log.info("Refresh Token 저장 완료: 이메일 = {}, 토큰 = {}", email, refreshToken);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int) (jwtUtil.getRefreshTokenExpireMillis() / 1000));
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

}
