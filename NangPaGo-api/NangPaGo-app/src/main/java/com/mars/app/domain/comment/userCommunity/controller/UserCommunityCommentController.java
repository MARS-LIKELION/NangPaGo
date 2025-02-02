package com.mars.app.domain.comment.userCommunity.controller;

import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.comment.userCommunity.dto.UserCommunityCommentRequestDto;
import com.mars.app.domain.comment.userCommunity.dto.UserCommunityCommentResponseDto;
import com.mars.common.dto.PageDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.domain.comment.userCommunity.service.UserCommunityCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "유저 커뮤니티 댓글 API", description = "유저 커뮤니티 게시물 '댓글' 관련 API")
@RequestMapping("/api/user-community/{id}/comment")
@RestController
public class UserCommunityCommentController {

    private final UserCommunityCommentService userCommunityCommentService;

    @Operation(summary = "댓글 목록 조회")
    @GetMapping
    public ResponseDto<PageDto<UserCommunityCommentResponseDto>> list(
        @PathVariable("id") Long id,
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "5") int pageSize) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userCommunityCommentService.pagedCommentsByUserCommunity(id, userId, pageNo, pageSize));
    }

    @Operation(summary = "댓글 작성")
    @AuthenticatedUser
    @PostMapping
    public ResponseDto<UserCommunityCommentResponseDto> create(
        @RequestBody UserCommunityCommentRequestDto requestDto,
        @PathVariable("id") Long id) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userCommunityCommentService.create(requestDto, userId, id), "댓글이 성공적으로 추가되었습니다.");
    }

    @Operation(summary = "댓글 수정")
    @AuthenticatedUser
    @PutMapping("/{commentId}")
    public ResponseDto<UserCommunityCommentResponseDto> update(
        @RequestBody UserCommunityCommentRequestDto requestDto,
        @PathVariable("commentId") Long commentId,
        @PathVariable("id") Long id) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userCommunityCommentService.update(commentId, userId, requestDto), "댓글이 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "댓글 삭제")
    @AuthenticatedUser
    @DeleteMapping("/{commentId}")
    public ResponseDto<Void> delete(
        @PathVariable("commentId") Long commentId,
        @PathVariable("id") Long id) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        userCommunityCommentService.delete(commentId, userId);
        return ResponseDto.of(null, "댓글이 성공적으로 삭제되었습니다.");
    }
}
