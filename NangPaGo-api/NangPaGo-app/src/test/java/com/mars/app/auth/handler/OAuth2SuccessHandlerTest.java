package com.mars.app.auth.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mars.app.auth.vo.OAuth2UserImpl;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.common.model.user.User;
import com.mars.common.util.web.CookieUtil;
import com.mars.common.util.web.JwtUtil;
import com.mars.app.domain.auth.service.OAuth2ProviderTokenService;
import com.mars.app.domain.auth.service.TokenService;
import com.mars.app.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CookieUtil cookieUtil;
    @Mock
    private TokenService tokenService;
    @Mock
    private OAuth2ProviderTokenService oauth2ProviderTokenService;
    @Mock
    private OAuth2AuthorizedClientManager oauth2AuthorizedClientManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;
    @Mock
    private OAuth2AuthorizedClient authorizedClient;
    @Mock
    private ClientRegistration clientRegistration;
    @Mock
    private OAuth2RefreshToken refreshToken;

    @InjectMocks
    private OAuth2SuccessHandler oauth2SuccessHandler;

    @DisplayName("OAuth2 인증 성공 시 토큰을 발급하고 홈페이지로 리다이렉트할 수 있다.")
    @Test
    void onAuthenticationSuccess() throws IOException {
        // given
        String email = "test@example.com";
        String provider = "GOOGLE";
        String role = "ROLE_USER";
        String mockRefreshTokenValue = "mock.refresh.token";

        User user = createUser(email, provider, role);
        Map<String, Object> attributes = createAttributes(provider);
        OAuth2UserImpl oauth2User = createOAuth2User(user.getId(), email, attributes);

        OAuth2AuthenticationToken oauth2Authentication = mock(OAuth2AuthenticationToken.class);
        when(oauth2Authentication.getPrincipal()).thenReturn(oauth2User);
        when(oauth2Authentication.getAuthorizedClientRegistrationId()).thenReturn(provider.toLowerCase());
        when(oauth2Authentication.getAuthorities())
            .thenReturn(Collections.singleton(new SimpleGrantedAuthority(role)));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(oauth2AuthorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
            .thenReturn(authorizedClient);
        when(authorizedClient.getRefreshToken()).thenReturn(refreshToken);
        when(refreshToken.getTokenValue()).thenReturn(mockRefreshTokenValue);
        when(authorizedClient.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getClientName()).thenReturn(provider);
        when(jwtUtil.createJwt(anyString(), anyLong(), anyString(), anyString(), anyLong()))
            .thenReturn("token");

        // when
        oauth2SuccessHandler.onAuthenticationSuccess(request, response, oauth2Authentication);

        // then
        verify(oauth2ProviderTokenService).renewOauth2ProviderToken(eq(provider), eq(mockRefreshTokenValue), eq(user.getId()));
        verify(tokenService).renewRefreshToken(eq(email), anyString());
        verify(cookieUtil).addCookie(eq(response), eq(CookieUtil.ACCESS_TOKEN_NAME), anyString(), anyLong(), eq(false));
        verify(cookieUtil).addCookie(eq(response), eq(CookieUtil.REFRESH_TOKEN_NAME), anyString(), anyLong(), eq(false));
    }

    @DisplayName("이미 다른 Provider로 가입된 이메일로 로그인 시도 시 에러 페이지로 리다이렉트할 수 있다.")
    @Test
    void onAuthenticationSuccess_DifferentProvider() throws IOException {
        // given
        String email = "test@example.com";
        String role = "ROLE_USER";
        String attemptProvider = "GOOGLE";
        String existingProvider = "KAKAO";
        User user = createUser(email, existingProvider, role);
        Map<String, Object> attributes = createAttributes(attemptProvider);
        OAuth2UserImpl oauth2User = createOAuth2User(user.getId(), email, attributes);

        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        oauth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(oauth2ProviderTokenService, never()).renewOauth2ProviderToken(anyString(), anyString(), anyLong());
        verify(tokenService, never()).renewRefreshToken(anyString(), anyString());
        verify(response).sendRedirect(any());
    }

    private User createUser(String email, String provider, String role) {
        return User.builder()
            .id(1L)
            .email(email)
            .oauth2Provider(OAuth2Provider.valueOf(provider))
            .role(role)
            .build();
    }

    private Map<String, Object> createAttributes(String provider) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("provider", provider);
        return attributes;
    }

    private OAuth2UserImpl createOAuth2User(Long userId, String email, Map<String, Object> attributes) {
        UserResponseDto userResponseDto = new UserResponseDto(userId, email, "ROLE_USER", null, null);
        return new OAuth2UserImpl(userResponseDto, attributes);
    }
}
