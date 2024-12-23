package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.enums.Provider;
import com.mars.NangPaGo.domain.user.factory.userinfo.OAuth2UserInfo;
import lombok.Builder;

@Builder
public record UserRequestDto(
        String name,
        String email,
        String provider,
        String providerId,
        String profileImageUrl
) {
    public static UserRequestDto fromOAuth2UserInfo(OAuth2UserInfo userInfo) {
        return UserRequestDto.builder()
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .provider(userInfo.getProvider())
                .providerId(userInfo.getProviderId())
                .build();
    }

    public User toEntity() {
        return User.builder()
            .name(this.name)
            .email(this.email)
            .role("ROLE_USER")
            .provider(Provider.from(this.provider))
            .providerId(this.providerId)
            .profileImageUrl(this.profileImageUrl)
            .build();
    }
}

