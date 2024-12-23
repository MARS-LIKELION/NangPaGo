package com.mars.NangPaGo.domain.user.controller;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.mars.NangPaGo.domain.user.service.RefreshTokenService;
import com.mars.NangPaGo.domain.user.util.JwtUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<?> refreshAccessToken(@CookieValue(value = "Authorization", required = false) String expiredToken) {
        if (expiredToken == null) {
            log.warn("Authorization 쿠키가 없습니다.");
            return ResponseEntity.status(UNAUTHORIZED).body("토큰이 없습니다.");
        }

        try {
            String refreshToken = refreshTokenService.findValidRefreshToken(expiredToken);
            String email = jwtUtil.extractEmailFromToken(refreshToken);
            String newAccessToken = jwtUtil.createAccessToken(email, "ROLE_USER", jwtUtil.getAccessTokenExpire());

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (IllegalArgumentException e) {
            log.error("리프레시 토큰이 유효하지 않거나 만료됨: {}", e.getMessage());
            return ResponseEntity.status(UNAUTHORIZED).body("만료된 리프레시 토큰입니다.");
        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 갱신 실패");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            refreshTokenService.invalidateRefreshToken(email);
            log.info("사용자 로그아웃: {}", email);
        }
        return ResponseEntity.ok("로그아웃 성공");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to /me");
            return ResponseEntity.status(UNAUTHORIZED).body("로그인되지 않은 상태");
        }

        String email = authentication.getName();
        log.info("Authenticated user: {}", email);
        return ResponseEntity.ok(Map.of("email", email));
    }
}
