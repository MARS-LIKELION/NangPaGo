package com.mars.NangPaGo.domain.user.controller;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.user.dto.MyPageDto;
import com.mars.NangPaGo.domain.user.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final MyPageService myPageService;

    @GetMapping("/mypage")
    public ResponseDto<MyPageDto> findMypage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of();
        }
        String email = authentication.getName();
        MyPageDto myPageDto = myPageService.myPage(email);

        return ResponseDto.of(myPageDto);
    }
}
