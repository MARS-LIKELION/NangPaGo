package com.mars.admin.domain.user.controller;

import com.mars.admin.domain.user.dto.UserDto;
import com.mars.admin.domain.user.service.UserService;
import com.mars.common.dto.ResponseDto;
import com.mars.common.enums.user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseDto<Page<UserDto>> userList(@RequestParam(defaultValue = "0") int page) {
        return ResponseDto.of(userService.getUserList(page), "");
    }

    @PutMapping("/ban")
    public ResponseDto<Void> banUser(@RequestParam long userId) {
        userService.updateUserStatus(userId, UserStatus.BANNED);
        return ResponseDto.of(null,"");
    }

    @PutMapping("/unban")
    public ResponseDto<Void> unBanUser(@RequestParam long userId) {
        userService.updateUserStatus(userId, UserStatus.BANNED);
        return ResponseDto.of(null,"");
    }
}
