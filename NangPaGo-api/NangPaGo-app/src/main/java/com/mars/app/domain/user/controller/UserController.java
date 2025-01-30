package com.mars.app.domain.user.controller;


import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.common.dto.PageDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.domain.auth.service.OAuth2ProviderTokenService;
import com.mars.common.exception.NPGExceptionType;
import com.mars.app.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.app.domain.recipe.dto.RecipeResponseDto;
import com.mars.common.dto.user.MyPageDto;
import com.mars.common.dto.user.UserInfoRequestDto;
import com.mars.common.dto.user.UserInfoResponseDto;
import com.mars.app.domain.user.service.UserService;
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
        Long userId = AuthenticationHolder.getCurrentUserId();
        MyPageDto myPageDto = userService.getMyPage(userId);

        return ResponseDto.of(myPageDto);
    }

    @AuthenticatedUser
    @GetMapping("/profile")
    public ResponseDto<UserInfoResponseDto> findUserInfo() {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getUserInfo(userId));
    }

    @AuthenticatedUser
    @GetMapping("/profile/check")
    public ResponseDto<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseDto.of(userService.isNicknameAvailable(nickname));
    }

    @AuthenticatedUser
    @PutMapping("/profile")
    public ResponseDto<UserInfoResponseDto> updateUserInfo(@RequestBody UserInfoRequestDto requestDto) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.updateUserInfo(requestDto, userId));
    }

    @GetMapping("/recipe/like")
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
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyLikedRecipes(userId, pageNo - 1, pageSize));
    }

    @AuthenticatedUser
    @GetMapping("/recipe/favorite")
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
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyFavorites(userId, pageNo - 1, pageSize));
    }

    @AuthenticatedUser
    @GetMapping("/recipe/comment")
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

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyComments(userId, pageNo - 1, pageSize));
    }

    @AuthenticatedUser
    @GetMapping("/deactivate")
    public ResponseDto<String> deactivateUser() throws IOException, InterruptedException {
        Long userId = AuthenticationHolder.getCurrentUserId();
        oauth2ProviderTokenService.deactivateUser(userId);
        return ResponseDto.of("");
    }
}
