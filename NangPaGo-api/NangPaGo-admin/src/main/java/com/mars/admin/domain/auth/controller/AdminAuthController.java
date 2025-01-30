package com.mars.admin.domain.auth.controller;

import com.mars.admin.domain.auth.service.TokenService;
import com.mars.admin.domain.user.service.UserService;
import com.mars.admin.component.auth.AuthenticationHolder;
import com.mars.common.dto.ResponseDto;
import com.mars.common.dto.user.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AdminAuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/status")
    public ResponseDto<Object> currentUser() {
        Long userId = AuthenticationHolder.getCurrentUserId();
        UserResponseDto currentUser = userService.getCurrentUser(userId);
        return ResponseDto.of(currentUser);
    }

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        tokenService.reissueTokens(request, response);
        return ResponseEntity.ok().build();
    }
}
