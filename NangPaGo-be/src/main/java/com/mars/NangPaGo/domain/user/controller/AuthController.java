package com.mars.NangPaGo.domain.user.controller;

import com.mars.NangPaGo.domain.user.entity.Refresh;
import com.mars.NangPaGo.domain.user.service.RefreshTokenService;
import com.mars.NangPaGo.domain.user.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue("RefreshToken") String refreshToken,
        HttpServletResponse response) {
        log.info("Received RefreshToken: {}", refreshToken);
        try {
            Refresh validRefreshToken = refreshTokenService.validateAndGetRefreshToken(refreshToken);

            String newAccessToken = jwtUtil.createAccessToken(validRefreshToken.getUsername(), "ROLE_USER", jwtUtil.getAccessTokenExpire());

            String newRefreshToken = jwtUtil.createRefreshToken(validRefreshToken.getUsername(), jwtUtil.getRefreshTokenExpire());
            refreshTokenService.saveRefreshToken(newRefreshToken, validRefreshToken.getUsername(), jwtUtil.getRefreshTokenExpire());

            Cookie refreshTokenCookie = new Cookie("RefreshToken", newRefreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (jwtUtil.getRefreshTokenExpire() / 1000));
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "RefreshToken", required = false) String refreshToken,
        HttpServletResponse response) {
        if (refreshToken != null) {
            refreshTokenService.deleteRefreshTokenByToken(refreshToken);
        }

        Cookie clearRefreshTokenCookie = new Cookie("RefreshToken", null);
        clearRefreshTokenCookie.setHttpOnly(true);
        clearRefreshTokenCookie.setSecure(true);
        clearRefreshTokenCookie.setPath("/");
        clearRefreshTokenCookie.setMaxAge(0);
        response.addCookie(clearRefreshTokenCookie);

        return ResponseEntity.ok("로그아웃 성공");
    }

}
