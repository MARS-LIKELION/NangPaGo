package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.RefreshToken;

import java.time.LocalDateTime;

public record RefreshTokenDto(
    String refreshToken,
    String email,
    LocalDateTime expiration
) {
    public static RefreshTokenDto from(RefreshToken refreshToken) {
        return new RefreshTokenDto(
            refreshToken.getRefreshToken(),
            refreshToken.getEmail(),
            refreshToken.getExpiration()
        );
    }

    public RefreshToken toEntity() {
        return RefreshToken.create(this.refreshToken, this.email, this.expiration);
    }
}
