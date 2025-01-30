package com.mars.admin.domain.user.controller;

import com.mars.admin.domain.user.dto.UserBanResponseDto;
import com.mars.admin.domain.user.dto.UserDetailResponseDto;
import com.mars.admin.domain.user.service.UserService;
import com.mars.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseDto<Page<UserDetailResponseDto>> userList(@RequestParam(defaultValue = "0") int page) {
        return ResponseDto.of(userService.getUserList(page), "");
    }

    @PutMapping("/ban")
    public ResponseDto<UserBanResponseDto> banUser(@RequestParam long userId) {
        UserBanResponseDto userBanResponseDto = userService.banUser(userId);
        return ResponseDto.of(userBanResponseDto, "");
    }

    @PutMapping("/unban")
    public ResponseDto<UserBanResponseDto> unbanUser(@RequestParam long userId) {
        UserBanResponseDto userBanResponseDto = userService.unbanUser(userId);
        return ResponseDto.of(userBanResponseDto, "");
    }
}
