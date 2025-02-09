package com.mars.app.domain.user_recipe.controller;

import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.user.message.UserNotificationMessagePublisher;
import com.mars.common.dto.ResponseDto;
import com.mars.app.domain.user_recipe.dto.UserRecipeLikeResponseDto;
import com.mars.app.domain.user_recipe.service.UserRecipeLikeService;
import com.mars.common.enums.user.UserNotificationEventCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "유저 커뮤니티 API", description = "유저 커뮤니티 게시물 좋아요 관련 API")
@RestController
@RequestMapping("/api/user-recipe/{id}/like")
public class UserRecipeLikeController {

    private final UserRecipeLikeService userRecipeLikeService;
    private final UserNotificationMessagePublisher userNotificationMessagePublisher;

    @Operation(summary = "게시물 좋아요 상태 조회")
    @AuthenticatedUser
    @GetMapping("/status")
    public ResponseDto<Boolean> getUserCommunityLikeStatus(@PathVariable("id") Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userRecipeLikeService.isLiked(id, userId));
    }

    @Operation(summary = "게시물 좋아요 토글")
    @AuthenticatedUser
    @PostMapping("/toggle")
    public ResponseDto<UserRecipeLikeResponseDto> toggleUserCommunityLike(@PathVariable("id") Long postId) {
        Long userId = AuthenticationHolder.getCurrentUserId();

        UserRecipeLikeResponseDto userRecipeLikeResponseDto = userRecipeLikeService.toggleLike(postId, userId);
        userNotificationMessagePublisher.createUserNotification(
            UserNotificationEventCode.USER_RECIPE_LIKE,
            userId,
            postId
        );
        return ResponseDto.of(userRecipeLikeResponseDto);
    }

    @Operation(summary = "게시물 좋아요 개수 조회")
    @GetMapping("/count")
    public ResponseDto<Long> getUserCommunityLikeCount(@PathVariable("id") Long id) {
        long likeCount = userRecipeLikeService.getLikeCount(id);
        return ResponseDto.of(likeCount);
    }
}
