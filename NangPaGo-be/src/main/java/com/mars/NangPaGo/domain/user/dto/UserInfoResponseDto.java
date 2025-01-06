package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserInfoResponseDto(
    String name,
    String nickName,
    String email,
    String provider
) {

    public static UserInfoResponseDto from(User user) {
        return UserInfoResponseDto.builder()
            .name(user.getName())
            .nickName(user.getNickname())
            .email(user.getEmail())
            .provider(user.getOauth2Provider().name())
            .build();
    }
}
