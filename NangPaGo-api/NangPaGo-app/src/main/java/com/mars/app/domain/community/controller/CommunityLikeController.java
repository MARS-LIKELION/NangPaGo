package com.mars.app.domain.community.controller;

import com.mars.common.dto.ResponseDto;
import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.community.dto.CommunityLikeResponseDto;
import com.mars.app.domain.community.service.CommunityLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 API", description = "커뮤니티 게시물 '좋아요' 관련 API")
@RequestMapping("/api/community")
@RestController
public class CommunityLikeController {

    private final CommunityLikeService communityLikeService;

    @Operation(summary = "게시물 좋아요 상태 조회")
    @AuthenticatedUser
    @GetMapping("/{id}/like/status")
    public ResponseDto<Boolean> getCommunityLikeStatus(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityLikeService.isLiked(id, userId));
    }

    @Operation(summary = "게시물 좋아요 토글 버튼")
    @AuthenticatedUser
    @PostMapping("/{id}/like/toggle")
    public ResponseDto<CommunityLikeResponseDto> toggleCommunityLike(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityLikeService.toggleLike(id, userId));
    }

    @Operation(summary = "게시물 좋아요 개수 조회")
    @GetMapping("/{id}/like/count")
    public ResponseDto<Long> getCommunityLikeCount(@PathVariable Long id) {
        long likeCount = communityLikeService.getLikeCount(id);
        return ResponseDto.of(likeCount);
    }
}
