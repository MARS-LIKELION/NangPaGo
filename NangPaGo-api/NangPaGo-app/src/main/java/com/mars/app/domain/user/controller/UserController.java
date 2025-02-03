package com.mars.app.domain.user.controller;


import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.community.dto.CommunityResponseDto;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.domain.auth.service.OAuth2ProviderTokenService;
import com.mars.common.dto.page.PageRequestVO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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

    @Operation(summary = "마이페이지 수정")
    @AuthenticatedUser
    @GetMapping("/profile")
    public ResponseDto<UserInfoResponseDto> findUserInfo() {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getUserDetailInfo(userId));
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

    @Operation(summary = "좋아요 한 레시피 조회")
    @GetMapping("/recipe/like")
    public ResponseDto<PageResponseDto<RecipeResponseDto>> getMyLikedRecipes(PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyLikedRecipes(userId, pageRequestVO));
    }

    @Operation(summary = "즐겨찾기 한 레시피 조회")
    @AuthenticatedUser
    @GetMapping("/recipe/favorite")
    public ResponseDto<PageResponseDto<RecipeFavoriteListResponseDto>> getMyFavorites(PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyFavorites(userId, pageRequestVO));
    }

    @Operation(summary = "작성한 게시글 조회")
    @AuthenticatedUser
    @GetMapping("/community/post")
    public ResponseDto<PageResponseDto<CommunityResponseDto>> getMyPosts(PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyPosts(userId, pageRequestVO));
    }

    @Operation(summary = "댓글 조회")
    @AuthenticatedUser
    @GetMapping("/recipe/comment")
    public ResponseDto<PageResponseDto<RecipeCommentResponseDto>> getMyComments(PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyComments(userId, pageRequestVO));
    }

    @AuthenticatedUser
    @GetMapping("/deactivate")
    public ResponseDto<String> deactivateUser() throws IOException, InterruptedException {
        Long userId = AuthenticationHolder.getCurrentUserId();
        oauth2ProviderTokenService.deactivateUser(userId);
        return ResponseDto.of("");
    }
}
