package com.mars.NangPaGo.domain.user.service;

import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getCurrentUser(Authentication authentication) {
        return UserResponseDto.from(userRepository.findByEmail(extractEmail(authentication))
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of));
    }

    private String extractEmail(Authentication authentication) {
        return Optional.ofNullable(authentication)
            .map(Authentication::getPrincipal)
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .orElseThrow(() -> NPGExceptionType.UNAUTHORIZED.of("알 수 없는 인증 객체입니다."));
    }
}
