package com.mars.app.auth.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mars.common.exception.NPGException;
import com.mars.common.util.web.CookieUtil;
import com.mars.common.util.web.JwtUtil;
import com.mars.app.domain.auth.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class OAuth2LogoutServiceTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private CookieUtil cookieUtil;
    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private OAuth2LogoutService oauth2LogoutService;

    @DisplayName("유효한 Refresh Token으로 로그아웃을 성공할 수 있다.")
    @Test
    void handleLogout_Success() {
        // given
        String refreshToken = "valid.refresh.token";
        String email = "test@example.com";

        when(jwtUtil.isExpired(refreshToken)).thenReturn(false);
        when(jwtUtil.getCategory(refreshToken)).thenReturn(CookieUtil.REFRESH_TOKEN_NAME);
        when(jwtUtil.getEmail(refreshToken)).thenReturn(email);
        when(refreshTokenRepository.existsByRefreshToken(refreshToken)).thenReturn(true);

        // when
        oauth2LogoutService.handleLogout(refreshToken, httpServletResponse);

        // then
        verify(refreshTokenRepository).deleteByEmail(email);
        verify(cookieUtil).invalidateCookie(httpServletResponse, CookieUtil.REFRESH_TOKEN_NAME);
        verify(cookieUtil).invalidateCookie(httpServletResponse, CookieUtil.ACCESS_TOKEN_NAME);
        verify(httpServletResponse).setStatus(HttpStatus.OK.value());
    }

    @DisplayName("만료된 Refresh Token으로 로그아웃 시도 시 예외가 발생한다.")
    @Test
    void handleLogout_ExpiredToken() {
        // given
        String expiredToken = "expired.refresh.token";
        when(jwtUtil.isExpired(expiredToken)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> oauth2LogoutService.handleLogout(expiredToken, httpServletResponse))
            .isInstanceOf(NPGException.class)
            .hasMessage("Refresh Token이 만료되었습니다.");
    }

    @DisplayName("존재하지 않는 Refresh Token으로 로그아웃 시도 시 예외가 발생한다.")
    @Test
    void handleLogout_NonExistentToken() {
        // given
        String nonExistentToken = "nonexistent.refresh.token";
        when(jwtUtil.isExpired(nonExistentToken)).thenReturn(false);
        when(jwtUtil.getCategory(nonExistentToken)).thenReturn(CookieUtil.REFRESH_TOKEN_NAME);
        when(refreshTokenRepository.existsByRefreshToken(nonExistentToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> oauth2LogoutService.handleLogout(nonExistentToken, httpServletResponse))
            .isInstanceOf(NPGException.class)
            .hasMessage("유효하지 않은 Refresh Token입니다.");
    }

    @DisplayName("잘못된 카테고리의 토큰으로 로그아웃 시도 시 예외가 발생한다.")
    @Test
    void handleLogout_InvalidTokenCategory() {
        // given
        String invalidCategoryToken = "invalid.category.token";
        when(jwtUtil.isExpired(invalidCategoryToken)).thenReturn(false);
        when(jwtUtil.getCategory(invalidCategoryToken)).thenReturn(CookieUtil.ACCESS_TOKEN_NAME);

        // when & then
        assertThatThrownBy(() -> oauth2LogoutService.handleLogout(invalidCategoryToken, httpServletResponse))
            .isInstanceOf(NPGException.class)
            .hasMessage("유효하지 않은 Refresh Token입니다.");
    }
}
