package com.mars.admin.domain.user.controller;

import com.mars.admin.domain.user.dto.UserDto;
import com.mars.admin.domain.user.service.UserService;
import com.mars.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseDto<Page<UserDto>> userList(@RequestParam(defaultValue = "0") int page) {
        return ResponseDto.of(userService.getUserList(page), "");
    }

    @GetMapping("/ban")
    public ResponseDto<Void> banUser(@RequestParam long userId) {
        userService.ban(userId);
        return ResponseDto.of(null,"");
    }

    @GetMapping("/unban")
    public ResponseDto<Void> unBanUser(@RequestParam long userId) {
        userService.unBan(userId);
        return ResponseDto.of(null,"");
    }
}
