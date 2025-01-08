package com.mars.NangPaGo.domain.user.controller;


import com.mars.NangPaGo.common.aop.auth.AuthenticatedUser;
import com.mars.NangPaGo.common.component.auth.AuthenticationHolder;
import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.user.dto.MyPageDto;
import com.mars.NangPaGo.domain.user.dto.UserInfoRequestDto;
import com.mars.NangPaGo.domain.user.dto.UserInfoResponseDto;
import com.mars.NangPaGo.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "회원정보 관련 API", description = "회원정보 조회, 수정")
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary = "마이페이지 조회")
    @AuthenticatedUser
    @GetMapping("/my-page")
    public ResponseDto<MyPageDto> findMyPage() {
        String email = AuthenticationHolder.getCurrentUserEmail();
        MyPageDto myPageDto = userService.getMyPage(email);

        return ResponseDto.of(myPageDto);
    }

    @AuthenticatedUser
    @GetMapping("/profile")
    public ResponseDto<UserInfoResponseDto> findUserInfo() {
        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(userService.getUserInfo(email));
    }

    @AuthenticatedUser
    @GetMapping("/profile/check")
    public ResponseDto<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseDto.of(userService.isNicknameAvailable(nickname));
    }

    @AuthenticatedUser
    @PutMapping("/profile")
    public ResponseDto<UserInfoResponseDto> updateUserInfo(@RequestBody UserInfoRequestDto requestDto) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(userService.updateUserInfo(requestDto, email));
    }
}
