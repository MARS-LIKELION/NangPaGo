package com.mars.admin.domain.user.dto;

import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.common.enums.user.UserStatus;
import com.mars.common.model.user.User;
import lombok.Builder;

@Builder
public record UserDetailResponseDto(
    Long id,
    String email,
    String nickname,
    OAuth2Provider oAuth2Provider,
    String createdAt,
    String updatedAt,
    UserStatus userStatus
) {

    public static UserDetailResponseDto from(User user) {
        return UserDetailResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .oAuth2Provider(user.getOauth2Provider())
            .createdAt(user.getCreatedAt().toString())
            .updatedAt(user.getUpdatedAt().toString())
            .userStatus(user.getUserStatus())
            .build();
    }
}
