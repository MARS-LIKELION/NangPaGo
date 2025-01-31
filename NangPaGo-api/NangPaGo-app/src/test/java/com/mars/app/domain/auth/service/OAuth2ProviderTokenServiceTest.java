package com.mars.app.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mars.app.domain.auth.client.OAuth2ApiClient;
import com.mars.app.domain.auth.factory.OAuth2TokenFactory;
import com.mars.app.domain.auth.factory.oauth2tokeninfo.GoogleTokenInfo;
import com.mars.app.domain.auth.factory.oauth2tokeninfo.KakaoTokenInfo;
import com.mars.app.domain.auth.factory.oauth2tokeninfo.NaverTokenInfo;
import com.mars.app.domain.auth.repository.OAuthProviderTokenRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.common.exception.NPGException;
import com.mars.common.model.auth.OAuthProviderToken;
import com.mars.common.model.user.User;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class OAuth2ProviderTokenServiceTest extends IntegrationTestSupport {

    @MockitoBean
    private HttpClient httpClient;

    @Autowired
    private OAuth2TokenFactory oauth2TokenFactory;
    @Autowired
    private OAuthProviderTokenRepository oauthProviderTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OAuth2ApiClient oauth2ApiClient;

    @Autowired
    private OAuth2ProviderTokenService oauth2ProviderTokenService;

    @AfterEach
    void tearDown() {
        oauthProviderTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("새로운 OAuth2 Provider 토큰을 저장할 수 있다.")
    @Test
    void renewOauth2ProviderToken_SaveNew() {
        // given
        String providerName = "GOOGLE";
        String refreshToken = "new-refresh-token";
        User user = createUser("test@example.com");
        userRepository.save(user);

        // when
        oauth2ProviderTokenService.renewOauth2ProviderToken(providerName, refreshToken, user.getId());

        // then
        OAuthProviderToken savedToken = oauthProviderTokenRepository
            .findByUserId(user.getId())
            .orElseThrow();

        assertThat(savedToken.getProviderName()).isEqualTo(providerName);
        assertThat(savedToken.getProviderRefreshToken()).isEqualTo(refreshToken);
        assertThat(savedToken.getUser().getId()).isEqualTo(user.getId());
    }

    @DisplayName("기존 OAuth2 Provider 토큰을 업데이트할 수 있다.")
    @Test
    void renewOauth2ProviderToken_Update() {
        // given
        String providerName = "GOOGLE";
        String oldRefreshToken = "old-refresh-token";
        String newRefreshToken = "new-refresh-token";
        User user = createUser("test@example.com");
        userRepository.save(user);

        OAuthProviderToken existingToken = OAuthProviderToken.builder()
            .providerName(providerName)
            .providerRefreshToken(oldRefreshToken)
            .user(user)
            .build();
        oauthProviderTokenRepository.save(existingToken);

        // when
        oauth2ProviderTokenService.renewOauth2ProviderToken(providerName, newRefreshToken, user.getId());

        // then
        OAuthProviderToken updatedToken = oauthProviderTokenRepository
            .findByUserId(user.getId())
            .orElseThrow();

        assertThat(updatedToken.getProviderRefreshToken()).isEqualTo(newRefreshToken);
    }

    @DisplayName("존재하지 않는 사용자 ID로 비활성화를 시도하면 예외가 발생한다.")
    @Test
    void deactivateUser_UserNotFound() {
        // when & then
        assertThatThrownBy(() -> oauth2ProviderTokenService.deactivateUser(999L))
            .isInstanceOf(NPGException.class)
            .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @DisplayName("Provider 이름으로 올바른 OAuth2TokenInfo 구현체를 가져올 수 있다.")
    @Test
    void getTokenInfoForDifferentProviders() {
        // given
        String googleProvider = "GOOGLE";
        String kakaoProvider = "KAKAO";
        String naverProvider = "NAVER";

        // when
        var googleTokenInfo = oauth2TokenFactory.from(googleProvider);
        var kakaoTokenInfo = oauth2TokenFactory.from(kakaoProvider);
        var naverTokenInfo = oauth2TokenFactory.from(naverProvider);

        // then
        assertThat(googleTokenInfo).isInstanceOf(GoogleTokenInfo.class);
        assertThat(kakaoTokenInfo).isInstanceOf(KakaoTokenInfo.class);
        assertThat(naverTokenInfo).isInstanceOf(NaverTokenInfo.class);
    }

    @DisplayName("사용자 계정을 비활성화할 수 있다.")
    @Test
    void deactivateUser() throws IOException, InterruptedException {
        // given
        User user = User.builder()
            .email("test@example.com")
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .build();
        userRepository.save(user);

        OAuthProviderToken token = OAuthProviderToken.of(
            user.getOauth2Provider().name(),
            "refresh-token",
            user
        );
        oauthProviderTokenRepository.save(token);

        // OAuth2 연결 해제 HTTP 응답 Mocking
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"access_token\":\"mock-access-token\"}");
        // 요청 Mocking
        when(httpClient.send(
            any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any())
        ).thenReturn(mockResponse);

        // when
        oauth2ProviderTokenService.deactivateUser(user.getId());

        // then
        User deactivatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(deactivatedUser.getLeftAt()).isNotNull();

        boolean tokenExists = oauthProviderTokenRepository
            .findByUserId(user.getId())
            .isPresent();
        assertThat(tokenExists).isFalse();
    }

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .build();
    }
}
