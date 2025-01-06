package com.mars.NangPaGo.domain.user.controller;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.user.dto.MyPageDto;
import com.mars.NangPaGo.domain.user.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "회원정보 관련 API", description = "회원정보 조회, 수정")
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final MyPageService myPageService;

    @Operation(summary = "마이페이지 조회")
    @GetMapping("/my-page")
    public ResponseDto<MyPageDto> findMyPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of();
        }
        String email = authentication.getName();
        MyPageDto myPageDto = myPageService.myPage(email);

        return ResponseDto.of(myPageDto);
    }
}
