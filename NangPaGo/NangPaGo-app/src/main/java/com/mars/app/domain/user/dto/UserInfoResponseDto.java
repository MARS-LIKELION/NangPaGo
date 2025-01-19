package com.mars.app.domain.user.dto;

import com.mars.app.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserInfoResponseDto(
    String nickname,
    String email
) {

    public static UserInfoResponseDto from(User user) {
        return UserInfoResponseDto.builder()
            .nickname(user.getNickname())
            .email(user.getEmail())
            .build();
    }
}
