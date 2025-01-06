package com.mars.NangPaGo.domain.auth.dto;

import com.mars.NangPaGo.domain.auth.entity.RefreshToken;

import java.time.LocalDateTime;

public record RefreshTokenDto(
    String email,
    String refreshToken,
    LocalDateTime expiration
) {
    public static RefreshTokenDto from(RefreshToken refreshToken) {
        return new RefreshTokenDto(
            refreshToken.getEmail(),
            refreshToken.getRefreshToken(),
            refreshToken.getExpiration()
        );
    }

    public RefreshToken toEntity() {
        return RefreshToken.create(this.email, this.refreshToken, this.expiration);
    }
}
