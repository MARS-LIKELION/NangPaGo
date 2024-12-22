package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.enums.Provider;
import com.mars.NangPaGo.domain.user.factory.userinfo.OAuth2UserInfo;
import lombok.Builder;


@Builder
public record UserRequestDto(
        String name,
        String nickname,
        String email,
        String profileImage,
        String provider,
        String providerId
) {
    public static UserRequestDto fromOAuth2UserInfo(OAuth2UserInfo userInfo) {
        return UserRequestDto.builder()
                .name(userInfo.getName())
                .nickname(userInfo.getName())
                .email(userInfo.getEmail())
                .profileImage(userInfo.getProfileImage())
                .provider(userInfo.getProvider())
                .providerId(userInfo.getProviderId())
                .build();
    }

    public User toEntity() {
        return User.builder()
                .name(this.name)
                .nickname(this.nickname)
                .email(this.email)
                .profileImage(this.profileImage)
                .provider(Provider.from(this.provider)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid provider: " + this.provider)))
                .providerId(this.providerId)
                .role("ROLE_USER")
                .build();
    }
}

