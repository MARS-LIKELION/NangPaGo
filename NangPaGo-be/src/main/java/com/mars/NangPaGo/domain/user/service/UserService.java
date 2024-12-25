package com.mars.NangPaGo.domain.user.service;

import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import com.mars.NangPaGo.domain.user.vos.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getCurrentUserStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("인증되지 않은 요청입니다.");
            return null; // 또는 적절한 예외 처리
        }

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = customOAuth2User.getName();

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("사용자가 데이터베이스에 존재하지 않습니다."));

        log.info("현재 사용자 상태: email={}, role={}", user.getEmail(), user.getRole());
        return UserResponseDto.from(user);
    }
}
