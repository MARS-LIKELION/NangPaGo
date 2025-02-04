package com.mars.admin.domain.user.controller;

import com.mars.admin.domain.user.dto.UserBanResponseDto;
import com.mars.admin.domain.user.dto.UserDetailResponseDto;
import com.mars.admin.domain.user.service.UserService;
import com.mars.admin.domain.user.sort.UserListSortType;
import com.mars.common.dto.ResponseDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.common.enums.user.UserStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public ResponseDto<PageResponseDto<UserDetailResponseDto>> userList(
        PageRequestVO pageRequestVO,
        @RequestParam(defaultValue = "ID_ASC") UserListSortType sort,
        @RequestParam(required = false) List<UserStatus> statuses,
        @RequestParam(required = false) List<OAuth2Provider> providers
    ) {
        return ResponseDto.of(userService.getUserList(pageRequestVO, sort, statuses, providers));
    }
}
