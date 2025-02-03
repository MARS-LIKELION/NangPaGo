package com.mars.app.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.mars.app.domain.auth.repository.RefreshTokenRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.exception.NPGException;
import com.mars.common.model.auth.RefreshToken;
import com.mars.common.util.web.CookieUtil;
import com.mars.common.util.web.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class TokenServiceTest extends IntegrationTestSupport {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CookieUtil cookieUtil;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenService tokenService;

    @DisplayName("유효한 Refresh Token으로 Access Token을 재발급할 수 있다.")
    @Test
    void reissueTokens_Success() {
        // given
        String email = "test@example.com";
        String testRefreshToken = testJwtProvider.createTestRefreshToken(email, "ROLE_USER");
        LocalDateTime expiration = LocalDateTime.now().plusSeconds(3600);
        RefreshToken refreshToken = RefreshToken.create(
            email,
            testRefreshToken,
            expiration);
        refreshTokenRepository.save(refreshToken);

        HttpServletRequest request = createRequestWithRefreshToken(testRefreshToken);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        tokenService.reissueTokens(request, response);

        // then
        Cookie accessTokenCookie = response.getCookie(CookieUtil.ACCESS_TOKEN_NAME);
        assertThat(accessTokenCookie).isNotNull();
        assertThat(jwtUtil.isExpired(accessTokenCookie.getValue())).isFalse();
    }

    @DisplayName("만료된 Refresh Token으로 재발급 시도 시 예외가 발생한다.")
    @Test
    void reissueTokens_ExpiredToken() {
        // given
        String expiredToken = jwtUtil.createJwt(
            CookieUtil.REFRESH_TOKEN_NAME,
            1L,
            "test@example.com",
            "ROLE_USER",
            -1000L);
        HttpServletRequest request = createRequestWithRefreshToken(expiredToken);
        HttpServletResponse response = new MockHttpServletResponse();

        // when & then
        assertThatThrownBy(() -> tokenService.reissueTokens(request, response))
            .isInstanceOf(NPGException.class)
            .hasMessage("Refresh Token이 만료되었습니다.");
    }

    @DisplayName("존재하지 않는 Refresh Token으로 재발급 시도 시 예외가 발생한다.")
    @Test
    void reissueTokens_NonExistentToken() {
        // given
        String nonExistentToken = jwtUtil.createJwt(
            CookieUtil.REFRESH_TOKEN_NAME,
            1L,
            "test@example.com",
            "ROLE_USER",
            3600000L);
        HttpServletRequest request = createRequestWithRefreshToken(nonExistentToken);
        HttpServletResponse response = new MockHttpServletResponse();

        // when & then
        assertThatThrownBy(() -> tokenService.reissueTokens(request, response))
            .isInstanceOf(NPGException.class)
            .hasMessage("유효하지 않은 Refresh Token 입니다.");
    }

    @DisplayName("토큰의 Category가 'refresh' 가 아닌 경우 예외가 발생한다.")
    @Test
    void reissueTokens_DifferentCategoryToken() {
        // given
        String notRefreshToken = jwtUtil.createJwt(
            "another_category",
            1L,
            "test@example.com",
            "ROLE_USER",
            3600000L);
        HttpServletRequest request = createRequestWithRefreshToken(notRefreshToken);
        HttpServletResponse response = new MockHttpServletResponse();

        // when & then
        assertThatThrownBy(() -> tokenService.reissueTokens(request, response))
            .isInstanceOf(NPGException.class)
            .hasMessage("유효하지 않은 Refresh Token입니다.");
    }

    @DisplayName("Refresh Token을 갱신할 수 있다.")
    @Test
    void renewRefreshToken_Success() {
        // given
        String email = "test@example.com";
        String refreshToken = jwtUtil.createJwt(CookieUtil.REFRESH_TOKEN_NAME, 1L, email, "ROLE_USER", 3600000L);

        // when
        tokenService.renewRefreshToken(email, refreshToken);

        // then
        Boolean result = refreshTokenRepository.existsByRefreshToken(refreshToken);
        assertThat(result).isTrue();
    }

    private HttpServletRequest createRequestWithRefreshToken(String refreshToken) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(CookieUtil.REFRESH_TOKEN_NAME, refreshToken));
        return request;
    }
}
