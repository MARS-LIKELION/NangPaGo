package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.enums.Gender;
import com.mars.NangPaGo.domain.user.enums.Provider;


public record UserResponseDto(
        String name,
        String nickname,
        String email,
        String profileImage,
        Provider provider
) {
    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage(),
        );
    }
}
