package com.mars.admin.domain.user.dto;

import com.mars.common.model.user.User;
import lombok.Builder;

@Builder
public record UserBanResponseDto(
    Long id,
    String email
) {
    public static UserBanResponseDto from(User user) {
        return UserBanResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .build();
    }
}
