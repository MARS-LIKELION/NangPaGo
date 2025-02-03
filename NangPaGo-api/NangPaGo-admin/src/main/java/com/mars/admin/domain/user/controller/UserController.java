package com.mars.admin.domain.user.controller;

import com.mars.admin.domain.user.dto.UserBanResponseDto;
import com.mars.admin.domain.user.dto.UserDetailResponseDto;
import com.mars.admin.domain.user.service.UserService;
import com.mars.admin.domain.user.sort.UserListSortType;
import com.mars.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

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

    @GetMapping
    public ResponseDto<Page<UserDetailResponseDto>> userList(@RequestParam(defaultValue = "0") int pageNo,
                                                             @RequestParam(defaultValue = "ID_ASC") UserListSortType sort) {
        return ResponseDto.of(userService.getUserList(pageNo, sort), "");
    }
}
