package com.mars.NangPaGo.domain.user.service;

// 필요한 클래스들을 import
import com.mars.NangPaGo.domain.user.entity.Refresh;
import com.mars.NangPaGo.domain.user.repository.RefreshTokenRepository;
import com.mars.NangPaGo.domain.user.util.JwtUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// 로그를 위한 Lombok 어노테이션과 의존성 주입을 위한 어노테이션
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // JWT 유틸리티 클래스와 리프레시 토큰 저장소를 주입받음
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // 리프레시 토큰을 저장하는 메서드
    public void saveRefreshToken(String token, String email, Long durationMs) {
        // 리프레시 토큰 엔티티 생성
        Refresh refreshToken = Refresh.builder()
            .token(token)
            .email(email)
            .expiration(LocalDateTime.now().plus(Duration.ofMillis(durationMs))) // 만료 시간 설정
            .build();
        // 리프레시 토큰 저장
        refreshTokenRepository.save(refreshToken);
        log.info("리프레시 토큰이 성공적으로 저장되었습니다: 사용자명 = {}, 토큰 = {}", email, token);
    }

    // 리프레시 토큰을 검증하고 반환하는 메서드
    public Refresh validateAndGetRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
            .filter(refreshToken -> !refreshToken.isExpired()) // 만료되지 않은 토큰 필터링
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다."));
    }

    // 리프레시 토큰을 찾거나 새로 생성하는 메서드
    public String findOrCreateRefreshToken(String email) {
        return refreshTokenRepository.findByEmail(email)
            .map(refresh -> handleExistingRefreshToken(refresh, email)) // 기존 토큰 처리
            .orElseGet(() -> createAndSaveNewRefreshToken(email)); // 새 토큰 생성
    }

    // 유효한 리프레시 토큰을 찾는 메서드
    public String findValidRefreshToken(String expiredToken) {
        String email = jwtUtil.extractEmailFromToken(expiredToken); // 토큰에서 이메일 추출

        Refresh refreshToken = refreshTokenRepository.findByEmail(email)
            .filter(token -> !token.isExpired()) // 만료되지 않은 토큰 필터링
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다."));

        return refreshToken.getToken();
    }

    // 기존 리프레시 토큰을 처리하는 메서드
    private String handleExistingRefreshToken(Refresh refresh, String email) {
        if (refresh.isExpired()) {
            log.info("기존 리프레시 토큰이 만료되었습니다. 새로 생성합니다.");
            return updateRefreshToken(refresh, email); // 만료된 경우 새로 생성
        }
        log.info("기존 리프레시 토큰이 유효합니다. 기존 토큰을 반환합니다.");
        return refresh.getToken(); // 유효한 경우 기존 토큰 반환
    }

    // 리프레시 토큰을 갱신하는 메서드
    private String updateRefreshToken(Refresh refresh, String email) {
        String newRefreshToken = jwtUtil.createRefreshToken(email, jwtUtil.getRefreshTokenExpire()); // 새 토큰 생성
        refresh.setToken(newRefreshToken); // 토큰 업데이트
        refresh.setExpiration(LocalDateTime.now().plus(Duration.ofMillis(jwtUtil.getRefreshTokenExpire()))); // 만료 시간 업데이트
        refreshTokenRepository.save(refresh); // 저장
        return newRefreshToken;
    }

    // 새 리프레시 토큰을 생성하고 저장하는 메서드
    private String createAndSaveNewRefreshToken(String email) {
        log.info("리프레시 토큰이 존재하지 않습니다. 새로 생성합니다.");
        String newRefreshToken = jwtUtil.createRefreshToken(email, jwtUtil.getRefreshTokenExpire()); // 새 토큰 생성
        refreshTokenRepository.save(
            Refresh.builder()
                .email(email)
                .token(newRefreshToken)
                .expiration(LocalDateTime.now().plus(Duration.ofMillis(jwtUtil.getRefreshTokenExpire()))) // 만료 시간 설정
                .build());
        return newRefreshToken;
    }

    // 이메일로 리프레시 토큰을 무효화하는 메서드
    public void invalidateRefreshToken(String email) {
        log.info("리프레시 토큰 무효화 시도: 이메일 = {}", email);
        int deletedCount = refreshTokenRepository.deleteByEmail(email);
        if (deletedCount > 0) {
            log.info("리프레시 토큰이 성공적으로 삭제되었습니다: 이메일 = {}", email);
        } else {
            log.warn("리프레시 토큰 삭제 실패: 이메일 = {}", email);
        }
    }
}