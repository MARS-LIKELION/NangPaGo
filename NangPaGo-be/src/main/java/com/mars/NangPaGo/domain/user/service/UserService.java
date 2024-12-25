package com.mars.NangPaGo.domain.user.service;

import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getCurrentUser(Authentication authentication) {
        log.info("Authentication 객체: {}", authentication);

        // 인증 정보 검증
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        // Principal에서 이메일 추출
        String email = extractEmail(authentication);
        log.info("인증된 사용자 이메일: {}", email);

        // 데이터베이스에서 사용자 조회
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
        log.info("DB에서 확인된 사용자: {}", user);

        // DTO 반환
        return UserResponseDto.from(user);
    }

    private String extractEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof String) {
            return (String) principal; // Principal이 이메일(String)인 경우
        }
        throw new IllegalStateException("알 수 없는 인증 객체입니다.");
    }
}
