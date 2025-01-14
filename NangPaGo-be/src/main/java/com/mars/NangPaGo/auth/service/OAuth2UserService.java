package com.mars.NangPaGo.auth.service;

import com.mars.NangPaGo.auth.enums.OAuth2Provider;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.user.dto.UserRequestDto;
import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.auth.factory.OAuth2UserInfoFactory;
import com.mars.NangPaGo.auth.factory.userinfo.OAuth2UserInfo;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import com.mars.NangPaGo.auth.vo.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.create(
            userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes()
        );
        return new CustomOAuth2User(UserResponseDto.from(findOrRegisterUser(userInfo)), oAuth2User.getAttributes());
    }

    public User findOrRegisterUser(OAuth2UserInfo userInfo) {
        return userRepository.findByEmail(userInfo.getEmail())
            .map(user -> validateOrThrow(user, userInfo.getProvider()))
            .orElseGet(() -> registerUser(userInfo));
    }

    private static User validateOrThrow(User user, String provider) {
        OAuth2Provider userProvider = user.getOauth2Provider();

        if (userProvider != OAuth2Provider.from(provider)) {
            throw NPGExceptionType.CONFLICT_DUPLICATE_PROVIDER.of(
                String.format("이미 %s로 가입된 이메일입니다.", userProvider.name())
            );
        }
        return user;
    }


    private User registerUser(OAuth2UserInfo userInfo) {
        return userRepository.save(UserRequestDto.fromOAuth2UserInfo(userInfo).toEntity());
    }
}
