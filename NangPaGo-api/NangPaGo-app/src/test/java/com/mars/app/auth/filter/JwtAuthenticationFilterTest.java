package com.mars.app.auth.filter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mars.common.util.web.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @DisplayName("유효한 JWT 토큰으로 인증에 성공할 수 있다.")
    @Test
    void authenticateWithValidToken() throws Exception {
        // given
        String token = "valid.jwt.token";
        Authentication mockAuth = mock(Authentication.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.isExpired(token)).thenReturn(false);
        when(jwtUtil.getAuthentication(token)).thenReturn(mockAuth);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).getAuthentication(token);
    }

    @DisplayName("만료된 JWT 토큰으로 인증을 실패처리할 수 있다.")
    @Test
    void failAuthenticationWithExpiredToken() throws Exception {
        // given
        String token = "expired.jwt.token";
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.isExpired(token)).thenReturn(true);
        when(response.getWriter()).thenReturn(writer);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @DisplayName("Authorization 헤더가 없는 경우 필터 체인을 계속 진행할 수 있다.")
    @Test
    void continueFilterChainWithoutAuthHeader() throws Exception {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).getAuthentication(anyString());
    }

    @DisplayName("잘못된 형식의 Authorization 헤더는 무시하고 필터 체인을 계속 진행할 수 있다.")
    @Test
    void continueFilterChainWithInvalidAuthHeader() throws Exception {
        // given
        when(request.getHeader("Authorization")).thenReturn("Invalid-Token-Format");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).getAuthentication(anyString());
    }
}
