package com.mars.NangPaGo.domain.user.service;

import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
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
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return UserResponseDto.from(userRepository.findByEmail(extractEmail(authentication))
            .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다.")));
    }

    private String extractEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            return (String) principal;
        }
        throw new IllegalStateException("알 수 없는 인증 객체입니다.");
    }
}
