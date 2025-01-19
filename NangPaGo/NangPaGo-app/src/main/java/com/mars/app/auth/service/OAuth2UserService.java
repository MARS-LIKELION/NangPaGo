package com.mars.app.auth.service;

import com.mars.app.auth.factory.OAuth2UserInfoFactory;
import com.mars.app.auth.factory.userinfo.OAuth2UserInfo;
import com.mars.app.auth.vo.OAuth2UserImpl;
import com.mars.app.domain.user.dto.UserRequestDto;
import com.mars.app.domain.user.dto.UserResponseDto;
import com.mars.app.domain.user.entity.User;
import com.mars.app.domain.user.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
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
        
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("provider", userInfo.getProvider().toUpperCase());
        
        return new OAuth2UserImpl(UserResponseDto.from(findOrRegisterUser(userInfo)), attributes);
    }

    private User findOrRegisterUser(OAuth2UserInfo userInfo) {
        return userRepository.findByEmail(userInfo.getEmail())
            .orElseGet(() -> registerUser(userInfo));
    }

    private User registerUser(OAuth2UserInfo userInfo) {
        return userRepository.save(UserRequestDto.fromOAuth2UserInfo(userInfo).toEntity());
    }
}
