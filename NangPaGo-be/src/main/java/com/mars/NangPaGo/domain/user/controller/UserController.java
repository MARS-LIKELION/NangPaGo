package com.mars.NangPaGo.domain.user.controller;

import com.mars.NangPaGo.domain.user.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
}
