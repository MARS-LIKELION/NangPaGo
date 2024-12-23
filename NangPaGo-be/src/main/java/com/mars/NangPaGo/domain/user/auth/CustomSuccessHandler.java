package com.mars.NangPaGo.domain.user.auth;

// 필요한 클래스들을 import
import com.mars.NangPaGo.domain.user.service.RefreshTokenService;
import com.mars.NangPaGo.domain.user.util.JwtUtil;
import com.mars.NangPaGo.domain.user.vos.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

// 로그를 위한 Lombok 어노테이션과 의존성 주입을 위한 어노테이션
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // JWT 유틸리티 클래스와 리프레시 토큰 서비스를 주입받음
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    // 인증 성공 시 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        // 인증된 사용자 정보를 가져옴
        CustomOAuth2User userDetails = (CustomOAuth2User) authentication.getPrincipal();
        String email = userDetails.getName(); // 사용자 이메일
        String role = extractRole(authentication); // 사용자 역할 추출
        log.info("인증 성공: 사용자 이메일 = {}, 역할 = {}", email, role); // 인증 성공 로그
        handleTokens(email, role, response); // 토큰 처리
        redirectToHome(request, response); // 홈으로 리다이렉트
    }

    // 인증 객체에서 역할을 추출하는 메서드
    private String extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .findFirst() // 첫 번째 권한을 가져옴
            .orElseThrow(() -> new IllegalStateException("권한 정보가 없습니다.")) // 권한이 없으면 예외 발생
            .getAuthority(); // 권한 이름 반환
    }

    // 토큰을 처리하는 메서드
    private void handleTokens(String email, String role, HttpServletResponse response) {
        // 액세스 토큰 생성
        String accessToken = jwtUtil.createAccessToken(email, role, jwtUtil.getAccessTokenExpire());
        // 리프레시 토큰 생성 또는 조회
        refreshTokenService.findOrCreateRefreshToken(email);
        log.info("JWT 발급 완료. AccessToken: {}", accessToken); // 토큰 발급 로그

        // 액세스 토큰을 쿠키에 추가
        addAccessTokenToCookie(accessToken, response);
    }

    // 액세스 토큰을 쿠키에 추가하는 메서드
    private void addAccessTokenToCookie(String accessToken, HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("Authorization", accessToken); // 쿠키 생성
        accessTokenCookie.setHttpOnly(true); // 클라이언트 스크립트에서 접근 불가
        accessTokenCookie.setSecure(true); // HTTPS에서만 전송
        accessTokenCookie.setPath("/"); // 쿠키 경로 설정
        accessTokenCookie.setMaxAge((int) (jwtUtil.getAccessTokenExpire() / 1000)); // 쿠키 만료 시간 설정
        response.addCookie(accessTokenCookie); // 응답에 쿠키 추가
    }

    // 홈으로 리다이렉트하는 메서드
    private void redirectToHome(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = "http://localhost:5173/"; // 리다이렉트할 URL
        log.info("리다이렉션 URL: {}", redirectUrl); // 리다이렉션 로그
        getRedirectStrategy().sendRedirect(request, response, redirectUrl); // 리다이렉트 수행
    }
}