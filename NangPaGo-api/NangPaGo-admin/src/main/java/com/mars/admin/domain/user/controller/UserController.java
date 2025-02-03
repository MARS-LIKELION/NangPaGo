package com.mars.admin.domain.user.controller;

import com.mars.admin.domain.user.dto.UserDetailResponseDto;
import com.mars.admin.domain.user.service.UserService;
import com.mars.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
    public ResponseDto<Page<UserDetailResponseDto>> userList(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "true") boolean asc,
                                                             @RequestParam(defaultValue = "id") String sortName) {
        if (asc) {
            return ResponseDto.of(userService.getUserList(page, Sort.Direction.ASC, sortName), "");
        }
        return ResponseDto.of(userService.getUserList(page, Sort.Direction.DESC, sortName), "");
    }
}
