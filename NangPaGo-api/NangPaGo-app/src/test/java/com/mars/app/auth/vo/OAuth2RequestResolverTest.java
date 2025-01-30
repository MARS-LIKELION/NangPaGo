package com.mars.app.auth.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OAuth2RequestResolverTest {

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    // Spy
    private DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    private OAuth2RequestResolver oAuth2RequestResolver;

    @BeforeEach
    void setUp() {
        // DefaultOAuth2AuthorizationRequestResolver 를 Spy 객체로 생성
        defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository,
            "/api/oauth2/authorization"
        );
        defaultResolver = spy(defaultResolver);

        // ReflectionTestUtils 를 통해 Spy 객체를 Setter 주입
        oAuth2RequestResolver = new OAuth2RequestResolver(clientRegistrationRepository, "/api/oauth2/authorization");
        ReflectionTestUtils.setField(oAuth2RequestResolver, "defaultResolver", defaultResolver);
    }

    @DisplayName("Google Provider 인 경우 offline access type과 consent prompt가 추가되어야 한다.")
    @Test
    void givenGoogleProvider_whenResolve_thenAddOfflineAccessTypeAndConsent() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("registration_id", "google");

        OAuth2AuthorizationRequest originalRequest = OAuth2AuthorizationRequest.authorizationCode()
            .authorizationUri("https://accounts.google.com/o/oauth2/auth")
            .clientId("client-id")
            .redirectUri("http://localhost:8080/callback")
            .attributes(attributes)
            .build();

        when(defaultResolver.resolve(httpServletRequest)).thenReturn(originalRequest);

        // when
        OAuth2AuthorizationRequest customizedRequest = oAuth2RequestResolver.resolve(httpServletRequest);

        // then
        assertThat(customizedRequest).isNotNull();
        assertThat(customizedRequest.getAdditionalParameters())
            .containsEntry("access_type", "offline")
            .containsEntry("prompt", "consent");
    }

    @DisplayName("Google Provider가 아닌 경우 추가 파라미터가 없어야 한다.")
    @Test
    void givenNonGoogleProvider_whenResolve_thenNoAdditionalParameters() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("registration_id", "github");

        OAuth2AuthorizationRequest originalRequest = OAuth2AuthorizationRequest.authorizationCode()
            .authorizationUri("https://github.com/login/oauth/authorize")
            .clientId("client-id")
            .redirectUri("http://localhost:8080/callback")
            .attributes(attributes)
            .build();

        when(defaultResolver.resolve(httpServletRequest)).thenReturn(originalRequest);

        // when
        OAuth2AuthorizationRequest customizedRequest = oAuth2RequestResolver.resolve(httpServletRequest);

        // then
        assertThat(customizedRequest).isNotNull();
        assertThat(customizedRequest.getAdditionalParameters())
            .doesNotContainKey("access_type")
            .doesNotContainKey("prompt");
    }

    @DisplayName("요청이 null인 경우 null을 반환해야 한다.")
    @Test
    void givenNullRequest_whenResolve_thenReturnNull() {
        // given
        when(defaultResolver.resolve(httpServletRequest)).thenReturn(null);

        // when
        OAuth2AuthorizationRequest result = oAuth2RequestResolver.resolve(httpServletRequest);

        // then
        assertThat(result).isNull();
    }
}
