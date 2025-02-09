package com.mars.app.domain.user_recipe.controller;

import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.common.dto.ResponseDto;
import com.mars.app.domain.user_recipe.dto.UserRecipeLikeResponseDto;
import com.mars.app.domain.user_recipe.service.UserRecipeLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "유저 커뮤니티 API", description = "유저 커뮤니티 게시물 좋아요 관련 API")
@RestController
@RequestMapping("/api/user-recipe")
public class UserRecipeLikeController {

    private final UserRecipeLikeService userRecipeLikeService;

    @Operation(summary = "게시물 좋아요 상태 조회")
    @AuthenticatedUser
    @GetMapping("/{id}/like/status")
    public ResponseDto<Boolean> getUserCommunityLikeStatus(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userRecipeLikeService.isLiked(id, userId));
    }

    @Operation(summary = "게시물 좋아요 토글")
    @AuthenticatedUser
    @PostMapping("/{id}/like/toggle")
    public ResponseDto<UserRecipeLikeResponseDto> toggleUserCommunityLike(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userRecipeLikeService.toggleLike(id, userId));
    }

    @Operation(summary = "게시물 좋아요 개수 조회")
    @GetMapping("/{id}/like/count")
    public ResponseDto<Long> getUserCommunityLikeCount(@PathVariable Long id) {
        long likeCount = userRecipeLikeService.getLikeCount(id);
        return ResponseDto.of(likeCount);
    }
}
