package com.mars.NangPaGo.domain.user.service;

import com.mars.NangPaGo.domain.user.dto.UserRequestDto;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.factory.OAuth2UserInfoFactory;
import com.mars.NangPaGo.domain.user.factory.userinfo.OAuth2UserInfo;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import com.mars.NangPaGo.domain.user.vo.CustomOAuth2User;
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
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("유저 데이터 조회: {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("제공자: {}", registrationId);

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.create(registrationId, oAuth2User.getAttributes());
        log.info("유저 생성: {}", userInfo.toString());

        return new CustomOAuth2User(userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> registerUser(userInfo)), oAuth2User.getAttributes());
    }

    private User registerUser(OAuth2UserInfo userInfo) {
        return userRepository.save(UserRequestDto.fromOAuth2UserInfo(userInfo).toEntity());
    }
}
