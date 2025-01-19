package com.mars.common.dto.user;

import com.mars.common.model.user.User;
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
