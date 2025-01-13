package com.mars.NangPaGo.auth.vo;

import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class OAuth2UserImpl implements OAuth2User {

    private final UserResponseDto userResponseDto;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userResponseDto.
            role()));
    }

    @Override
    public String getName() {
        return principalValue();
    }

    private String principalValue() {
        return userResponseDto.email();
    }
}
