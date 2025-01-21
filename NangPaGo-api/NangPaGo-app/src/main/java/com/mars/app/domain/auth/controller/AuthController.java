package com.mars.app.domain.auth.controller;

import com.mars.common.dto.ResponseDto;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.auth.service.TokenService;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.app.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "사용자 인증 API", description = "인증 상태 조회, 엑세스 토큰 재발급 등")
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @Operation(summary = "Email, Role 정보 조회")
    @GetMapping("/status")
    public ResponseDto<Object> currentUser() {
        String email = AuthenticationHolder.getCurrentUserEmail();
        UserResponseDto currentUser = userService.getCurrentUser(email);
        return ResponseDto.of(currentUser);
    }

    @Operation(summary = "Access token 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        tokenService.reissueTokens(request, response);
        return ResponseEntity.ok().build();
    }
}
