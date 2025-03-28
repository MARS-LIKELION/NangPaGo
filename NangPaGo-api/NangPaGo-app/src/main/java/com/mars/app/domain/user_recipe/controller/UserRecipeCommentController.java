package com.mars.app.domain.user_recipe.controller;

import com.mars.app.aop.audit.AuditLog;
import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.user.message.UserNotificationMessagePublisher;
import com.mars.app.domain.user_recipe.dto.comment.UserRecipeCommentRequestDto;
import com.mars.app.domain.user_recipe.dto.comment.UserRecipeCommentResponseDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.domain.user_recipe.service.UserRecipeCommentService;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.enums.audit.AuditActionType;
import com.mars.common.enums.user.UserNotificationEventCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "유저 레시피 댓글 API", description = "유저 레시피 게시물 '댓글' 관련 API")
@RequestMapping("/api/user-recipe/{id}/comment")
@RestController
public class UserRecipeCommentController {

    private final UserRecipeCommentService userRecipeCommentService;
    private final UserNotificationMessagePublisher userNotificationMessagePublisher;

    @Operation(summary = "댓글 목록 조회")
    @GetMapping
    public ResponseDto<PageResponseDto<UserRecipeCommentResponseDto>> list(
        @PathVariable("id") Long id,
        PageRequestVO pageRequestVO
    ) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userRecipeCommentService.pagedCommentsByUserRecipe(id, userId, pageRequestVO));
    }

    @Operation(summary = "댓글 작성")
    @AuditLog(action = AuditActionType.USER_RECIPE_COMMENT_CREATE, dtoType = UserRecipeCommentRequestDto.class)
    @AuthenticatedUser
    @PostMapping
    public ResponseDto<UserRecipeCommentResponseDto> create(
        @RequestBody UserRecipeCommentRequestDto requestDto,
        @PathVariable("id") Long postId) {

        Long userId = AuthenticationHolder.getCurrentUserId();

        UserRecipeCommentResponseDto responseDto = userRecipeCommentService.create(requestDto, userId, postId);
        userNotificationMessagePublisher.createUserNotification(
            UserNotificationEventCode.USER_RECIPE_COMMENT,
            userId,
            postId
        );

        return ResponseDto.of(responseDto);
    }

    @Operation(summary = "댓글 수정")
    @AuditLog(action = AuditActionType.USER_RECIPE_COMMENT_UPDATE, dtoType = UserRecipeCommentRequestDto.class)
    @AuthenticatedUser
    @PutMapping("/{commentId}")
    public ResponseDto<UserRecipeCommentResponseDto> update(
        @RequestBody UserRecipeCommentRequestDto requestDto,
        @PathVariable("commentId") Long commentId,
        @PathVariable("id") Long id) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(
            userRecipeCommentService.update(requestDto, commentId, userId), "댓글이 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "댓글 삭제")
    @AuditLog(action = AuditActionType.USER_RECIPE_COMMENT_DELETE)
    @AuthenticatedUser
    @DeleteMapping("/{commentId}")
    public ResponseDto<Void> delete(
        @PathVariable("commentId") Long commentId,
        @PathVariable("id") Long id) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        userRecipeCommentService.delete(commentId, userId);
        return ResponseDto.of(null, "댓글이 성공적으로 삭제되었습니다.");
    }
}
