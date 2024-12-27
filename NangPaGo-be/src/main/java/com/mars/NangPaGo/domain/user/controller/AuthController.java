package com.mars.NangPaGo.domain.user.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
import com.mars.NangPaGo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @GetMapping("/status")
    public ResponseDto<Object> currentUser(Authentication authentication) {
        if (isNeedLogin(authentication)) {
            return ResponseDto.of("", "인증되지 않은 상태");
        }

        UserResponseDto currentUser = userService.getCurrentUser(authentication);
        return ResponseDto.of(currentUser);
    }

    private boolean isNeedLogin(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated();
    }
}
