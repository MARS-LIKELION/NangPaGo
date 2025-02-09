package com.mars.app.domain.user_recipe.controller;

import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.user_recipe.message.UserRecipeLikeMessagePublisher;
import com.mars.app.domain.user_recipe.event.UserRecipeLikeSseService;
import com.mars.common.dto.ResponseDto;
import com.mars.app.domain.user_recipe.dto.like.UserRecipeLikeResponseDto;
import com.mars.app.domain.user_recipe.service.UserRecipeLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Tag(name = "유저 레시피 API", description = "유저 레시피 좋아요, SSE 구독")
@RestController
@RequestMapping("/api/user-recipe/{id}/like")
public class UserRecipeLikeController {

    private final UserRecipeLikeService userRecipeLikeService;
    private final UserRecipeLikeMessagePublisher userRecipeLikeMessagePublisher;
    private final UserRecipeLikeSseService userRecipeLikeSseService;

    @Operation(summary = "유저 레시피 좋아요 상태 조회")
    @AuthenticatedUser
    @GetMapping("/status")
    public ResponseDto<Boolean> getUserCommunityLikeStatus(@PathVariable("id") Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userRecipeLikeService.isLiked(id, userId));
    }

    @Operation(summary = "유저 레시피 좋아요 토글")
    @AuthenticatedUser
    @PostMapping("/toggle")
    public ResponseDto<UserRecipeLikeResponseDto> toggleUserCommunityLike(@PathVariable("id") Long postId) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        UserRecipeLikeResponseDto userRecipeLikeResponseDto = userRecipeLikeMessagePublisher.toggleLike(postId, userId);

        return ResponseDto.of(userRecipeLikeResponseDto);
    }

    @Operation(summary = "유저 레시피 좋아요 개수 조회")
    @GetMapping("/count")
    public ResponseDto<Long> getUserCommunityLikeCount(@PathVariable("id") Long id) {
        long likeCount = userRecipeLikeService.getLikeCount(id);
        return ResponseDto.of(likeCount);
    }

    @Operation(summary = "유저 레시피 좋아요 개수 변경 SSE 이벤트 구독")
    @GetMapping("/notification/subscribe")
    public SseEmitter streamLikes(@PathVariable("id") Long id) {
        return userRecipeLikeSseService.createEmitter(id);
    }
}
