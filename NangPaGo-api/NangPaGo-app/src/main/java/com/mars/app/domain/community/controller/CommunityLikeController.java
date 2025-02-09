package com.mars.app.domain.community.controller;

import com.mars.app.domain.community.event.CommunityLikeSseService;
import com.mars.app.domain.community.message.like.CommunityLikeMessagePublisher;
import com.mars.app.domain.user.message.UserNotificationMessagePublisher;
import com.mars.common.dto.ResponseDto;
import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.community.dto.like.CommunityLikeResponseDto;
import com.mars.app.domain.community.service.CommunityLikeService;
import com.mars.common.enums.user.UserNotificationEventCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 좋아요 API", description = "커뮤니티 게시물 좋아요, SSE 구독")
@RequestMapping("/api/community/{id}/like")
@RestController
public class CommunityLikeController {

    private final CommunityLikeService communityLikeService;
    private final CommunityLikeMessagePublisher communityLikeMessagePublisher;
    private final CommunityLikeSseService communityLikeSseService;

    @Operation(summary = "게시물 좋아요 상태 조회")
    @AuthenticatedUser
    @GetMapping("/status")
    public ResponseDto<Boolean> getCommunityLikeStatus(@PathVariable("id") Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityLikeService.isLiked(id, userId));
    }

    @Operation(summary = "게시물 좋아요 토글 버튼")
    @AuthenticatedUser
    @PostMapping("/toggle")
    public ResponseDto<CommunityLikeResponseDto> toggleCommunityLike(@PathVariable("id") Long postId) {
        Long userId = AuthenticationHolder.getCurrentUserId();

        CommunityLikeResponseDto communityLikeResponseDto = communityLikeMessagePublisher.toggleLike(postId, userId);
        return ResponseDto.of(communityLikeResponseDto);
    }

    @Operation(summary = "게시물 좋아요 개수 조회")
    @GetMapping("/count")
    public ResponseDto<Long> getCommunityLikeCount(@PathVariable("id") Long id) {
        long likeCount = communityLikeService.getLikeCount(id);
        return ResponseDto.of(likeCount);
    }

    @Operation(summary = "게시물 총 좋아요 개수 변경 SSE 이벤트 구독")
    @GetMapping("/notification/subscribe")
    public SseEmitter streamLikes(@PathVariable("id") Long id) {
        return communityLikeSseService.createEmitter(id);
    }
}
