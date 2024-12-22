package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;

public record UserResponseDto(
        String role,
        String name,
        String nickname,
        String email
) {
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getRole(),
                user.getName(),
                user.getNickname(),
                user.getEmail()
        );
    }
}
