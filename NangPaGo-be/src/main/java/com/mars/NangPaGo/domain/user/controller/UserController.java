package com.mars.NangPaGo.domain.user.controller;


import com.mars.NangPaGo.common.aop.auth.AuthenticatedUser;
import com.mars.NangPaGo.common.component.auth.AuthenticationHolder;
import com.mars.NangPaGo.common.dto.PageDto;
import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.auth.service.OAuth2ProviderTokenService;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.NangPaGo.domain.recipe.dto.RecipeResponseDto;
import com.mars.NangPaGo.domain.user.dto.MyPageDto;
import com.mars.NangPaGo.domain.user.dto.UserInfoRequestDto;
import com.mars.NangPaGo.domain.user.dto.UserInfoResponseDto;
import com.mars.NangPaGo.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
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
    private final OAuth2ProviderTokenService oauth2ProviderTokenService;

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

    @GetMapping("/likes/recipes")
    public ResponseDto<PageDto<RecipeResponseDto>> getMyLikedRecipes(
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "7") int pageSize
    ) {
        if (pageNo < 1) {
            throw NPGExceptionType.BAD_REQUEST_INVALID_PAGE_NO.of();
        }
        if (pageSize < 1) {
            throw NPGExceptionType.BAD_REQUEST_INVALID_PAGE_SIZE.of();
        }

        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(userService.getMyLikedRecipes(email, pageNo - 1, pageSize));
    }

    @AuthenticatedUser
    @GetMapping("/favorites/recipes")
    public ResponseDto<PageDto<RecipeFavoriteListResponseDto>> getMyFavorites(
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "7") int pageSize
    ) {
        if (pageNo < 1) {
            throw NPGExceptionType.BAD_REQUEST_INVALID_PAGE_NO.of();
        }
        if (pageSize < 1) {
            throw NPGExceptionType.BAD_REQUEST_INVALID_PAGE_SIZE.of();
        }

        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(userService.getMyFavorites(email, pageNo - 1, pageSize));
    }

    @AuthenticatedUser
    @GetMapping("/comments")
    public ResponseDto<PageDto<RecipeCommentResponseDto>> getMyComments(
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "7") int pageSize
    ) {
        if (pageNo < 1) {
            throw NPGExceptionType.BAD_REQUEST_INVALID_PAGE_NO.of();
        }
        if (pageSize < 1) {
            throw NPGExceptionType.BAD_REQUEST_INVALID_PAGE_SIZE.of();
        }

        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(userService.getMyComments(email, pageNo - 1, pageSize));
    }

    @AuthenticatedUser
    @GetMapping("/deactivate")
    public ResponseDto<String> deactivateUser() throws IOException, InterruptedException {
        String email = AuthenticationHolder.getCurrentUserEmail();
        oauth2ProviderTokenService.deactivateUser(email);
        return ResponseDto.of("");
    }
}
