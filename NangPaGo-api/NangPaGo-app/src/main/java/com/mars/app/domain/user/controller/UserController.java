package com.mars.app.domain.user.controller;


import com.mars.app.aop.audit.AuditLog;
import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.community.dto.CommunityListResponseDto;
import com.mars.app.domain.recipe.dto.comment.RecipeCommentListResponseDto;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.domain.auth.service.OAuth2ProviderTokenService;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.app.domain.recipe.dto.RecipeListResponseDto;
import com.mars.common.dto.user.MyPageDto;
import com.mars.common.dto.user.UserInfoRequestDto;
import com.mars.common.dto.user.UserInfoResponseDto;
import com.mars.app.domain.user.service.UserService;
import com.mars.common.enums.audit.AuditActionType;
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

    @Operation(summary = "마이페이지 조회")
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

    @Operation(summary = "마이페이지 중 닉네임 수정")
    @AuthenticatedUser
    @AuditLog(action = AuditActionType.USER_INFO_UPDATE, dtoType = UserInfoRequestDto.class)
    @PutMapping("/profile")
    public ResponseDto<UserInfoResponseDto> updateUserInfo(@RequestBody UserInfoRequestDto requestDto) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.updateUserInfo(requestDto, userId));
    }

    @Operation(summary = "좋아요 한 레시피 조회")
    @GetMapping("/recipe/like/list")
    public ResponseDto<PageResponseDto<RecipeListResponseDto>> getMyLikedRecipes(PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyLikedRecipes(userId, pageRequestVO));
    }

    @Operation(summary = "즐겨찾기 한 레시피 조회")
    @AuthenticatedUser
    @GetMapping("/recipe/favorite/list")
    public ResponseDto<PageResponseDto<RecipeListResponseDto>> getMyFavorites(PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyFavorites(userId, pageRequestVO));
    }

    @Operation(summary = "작성한 게시글 조회")
    @AuthenticatedUser
    @GetMapping("/community/list")
    public ResponseDto<PageResponseDto<CommunityListResponseDto>> getMyPosts(PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyPosts(userId, pageRequestVO));
    }

    @Operation(summary = "댓글 조회")
    @AuthenticatedUser
    @GetMapping("/recipe/comment/list")
    public ResponseDto<PageResponseDto<RecipeCommentListResponseDto>> getMyComments(PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userService.getMyComments(userId, pageRequestVO));
    }

    @AuthenticatedUser
    @AuditLog(action = AuditActionType.USER_DEACTIVATE)
    @GetMapping("/deactivate")
    public ResponseDto<String> deactivateUser() throws IOException, InterruptedException {
        Long userId = AuthenticationHolder.getCurrentUserId();
        oauth2ProviderTokenService.deactivateUser(userId);
        return ResponseDto.of("");
    }
}
