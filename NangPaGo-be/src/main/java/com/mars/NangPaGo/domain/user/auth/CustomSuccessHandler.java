package com.mars.NangPaGo.domain.user.auth;

import com.mars.NangPaGo.domain.user.util.JwtUtil;
import com.mars.NangPaGo.domain.user.vo.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String email = customUserDetails.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("권한 정보가 없습니다."))
                .getAuthority();

        String accessToken = jwtUtil.createAccessToken(email, role, jwtUtil.getAccessTokenExpire());
        String refreshToken = jwtUtil.createRefreshToken(email, jwtUtil.getRefreshTokenExpire());

        Cookie accessTokenCookie = new Cookie("Authorization", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge((int) (jwtUtil.getAccessTokenExpire() / 1000));
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("RefreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) (jwtUtil.getRefreshTokenExpire() / 1000));
        response.addCookie(refreshTokenCookie);

        response.sendRedirect("http://localhost:5173/");
    }
}
