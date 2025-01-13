package com.mars.NangPaGo.auth.service;

import com.mars.NangPaGo.auth.enums.OAuth2Provider;
import com.mars.NangPaGo.auth.factory.userinfo.KakaoUserInfo;
import com.mars.NangPaGo.auth.factory.userinfo.OAuth2UserInfo;
import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import com.mars.NangPaGo.support.IntegrationTestSupport;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuth2UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuth2UserService oAuth2UserService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    private OAuth2UserInfo createKakaoUserInfo(String email, String providerId) {
        return new KakaoUserInfo(Map.of(
            "kakao_account", Map.of("email", email),
            "id", providerId
        ));
    }

    private User createUser(String email, OAuth2Provider provider) {
        return User.builder()
            .email(email)
            .oauth2Provider(provider)
            .build();
    }

    @Transactional
    @DisplayName("이미 동일 Provider로 가입된 사용자가 있으면 예외 발생")
    @Test
    void shouldThrowExceptionWhenDuplicateProviderExists() {
        // given
        String email = "test@example.com";
        OAuth2Provider provider = OAuth2Provider.KAKAO;

        User existingUser = createUser(email, provider);
        userRepository.save(existingUser);

        OAuth2UserInfo userInfo = createKakaoUserInfo(email, "kakao123");

        // when, then
        assertThatThrownBy(() -> oAuth2UserService.findOrRegisterUser(userInfo))
            .isInstanceOf(NPGException.class)
            .hasMessageContaining("KAKAO로 가입된 동일한 이메일이 존재합니다.");
    }

    @Transactional
    @DisplayName("다른 Provider로 동일한 이메일로 가입된 사용자 경고")
    @Test
    void shouldLogWarningWhenOtherProviderExists() {
        // given
        String email = "test@example.com";
        OAuth2Provider existingProvider = OAuth2Provider.GOOGLE;

        User existingUser = createUser(email, existingProvider);
        userRepository.save(existingUser);

        OAuth2UserInfo userInfo = createKakaoUserInfo(email, "kakao123");

        // when
        User result = oAuth2UserService.findOrRegisterUser(userInfo);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getOauth2Provider()).isEqualTo(existingProvider);
    }

    @Transactional
    @DisplayName("새로운 사용자 등록 테스트")
    @Test
    void shouldRegisterNewUserWhenNotExists() {
        // given
        String email = "newuser@example.com";
        OAuth2Provider provider = OAuth2Provider.KAKAO;

        OAuth2UserInfo userInfo = createKakaoUserInfo(email, "kakao123");

        // when
        User result = oAuth2UserService.findOrRegisterUser(userInfo);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getOauth2Provider()).isEqualTo(provider);
        assertThat(userRepository.findByEmail(email)).isPresent();
    }
}
