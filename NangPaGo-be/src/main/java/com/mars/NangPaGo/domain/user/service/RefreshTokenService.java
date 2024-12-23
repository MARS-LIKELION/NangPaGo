package com.mars.NangPaGo.domain.user.service;

import com.mars.NangPaGo.domain.user.entity.Refresh;
import com.mars.NangPaGo.domain.user.repository.RefreshTokenRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String token, String username, Long durationMs) {
        try {
            Refresh refreshToken = Refresh.builder()
                .token(token)
                .username(username)
                .expiration(LocalDateTime.now().plus(Duration.ofMillis(durationMs)))
                .build();
            refreshTokenRepository.save(refreshToken);
        } catch (Exception e) {
            log.error("리프레시 토큰 저장 실패: ", e);
            throw e;
        }
    }

    public Refresh validateAndGetRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
            .filter(refreshToken -> !refreshToken.isExpired())
            .orElseThrow(() -> new IllegalArgumentException("Invalid or expired refresh token"));
    }

    public void deleteRefreshTokenByToken(String token) {
        refreshTokenRepository.findByToken(token)
            .ifPresent(refreshTokenRepository::delete);
    }
}
