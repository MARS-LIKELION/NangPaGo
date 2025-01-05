package com.mars.NangPaGo.domain.auth.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
import com.mars.NangPaGo.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "사용자 인증 API", description = "사용자 인증 관련 API")
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final UserService userService;

    @GetMapping("/status")
    public ResponseDto<Object> currentUser() {
        UserResponseDto currentUser = userService.getCurrentUser();
        return ResponseDto.of(currentUser);
    }
}
