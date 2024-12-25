package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;

public record UserResponseDto(
    String email,
    String role
) {
    public static UserResponseDto from(User user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new IllegalStateException("사용자 역할이 설정되지 않았습니다: " + user.getEmail());
        }
        return new UserResponseDto(user.getEmail(), user.getRole());
    }
}
