package com.mars.app.domain.comment.community.controller;

import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.domain.comment.community.dto.CommunityCommentRequestDto;
import com.mars.app.domain.comment.community.dto.CommunityCommentResponseDto;
import com.mars.app.domain.comment.community.service.CommunityCommentService;
import com.mars.common.dto.page.PageRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 댓글 API", description = "커뮤니티 게시물 '댓글' 관련 API")
@RequestMapping("/api/community/{id}/comment")
@RestController
public class CommunityCommentController {

    private final CommunityCommentService communityCommentService;

    @Operation(summary = "댓글 목록 조회")
    @GetMapping
    public ResponseDto<PageResponseDto<CommunityCommentResponseDto>> list(
        @PathVariable("id") Long id,
        PageRequestVO pageRequestVO
    ) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityCommentService.pagedCommentsByCommunity(id, userId, pageRequestVO));
    }

    @Operation(summary = "댓글 작성")
    @AuthenticatedUser
    @PostMapping
    public ResponseDto<CommunityCommentResponseDto> create(
        @RequestBody CommunityCommentRequestDto requestDto,
        @PathVariable("id") Long id) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityCommentService.create(requestDto, userId, id), "댓글이 성공적으로 추가되었습니다.");
    }

    @Operation(summary = "댓글 수정")
    @AuthenticatedUser
    @PutMapping("/{commentId}")
    public ResponseDto<CommunityCommentResponseDto> update(
        @RequestBody CommunityCommentRequestDto requestDto,
        @PathVariable("commentId") Long commentId,
        @PathVariable String id) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityCommentService.update(commentId, userId, requestDto), "댓글이 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "댓글 삭제")
    @AuthenticatedUser
    @DeleteMapping("/{commentId}")
    public ResponseDto<Void> delete(
        @PathVariable("commentId") Long commentId,
        @PathVariable String id) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        communityCommentService.delete(commentId, userId);
        return ResponseDto.of(null, "댓글이 성공적으로 삭제되었습니다.");
    }
}
