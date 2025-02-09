package com.mars.app.domain.recipe.controller;

import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.recipe.dto.comment.RecipeCommentRequestDto;
import com.mars.app.domain.recipe.dto.comment.RecipeCommentResponseDto;
import com.mars.app.domain.recipe.service.RecipeCommentService;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.enums.audit.AuditActionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.mars.app.aop.audit.AuditLog;

@RequiredArgsConstructor
@Tag(name = "레시피 댓글 API", description = "레시피 댓글 CRUD, Count 조회")
@RequestMapping("/api/recipe/{recipeId}/comment")
@RestController
public class RecipeCommentController {

    private final RecipeCommentService recipeCommentService;

    @Operation(summary = "레시피 댓글 전체 조회")
    @GetMapping
    public ResponseDto<PageResponseDto<RecipeCommentResponseDto>> list(
        @PathVariable("recipeId") Long recipeId, PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeCommentService.pagedCommentsByRecipe(recipeId, userId, pageRequestVO));
    }

    @AuditLog(action = AuditActionType.RECIPE_COMMENT_CREATE, dtoType = RecipeCommentRequestDto.class)
    @Operation(summary = "레시피 댓글 생성")
    @AuthenticatedUser
    @PostMapping
    public ResponseDto<RecipeCommentResponseDto> create(
        @RequestBody RecipeCommentRequestDto requestDto,
        @PathVariable("recipeId") Long recipeId) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeCommentService.create(requestDto, userId, recipeId));
    }

    @AuditLog(action = AuditActionType.RECIPE_COMMENT_UPDATE, dtoType = RecipeCommentRequestDto.class)
    @Operation(summary = "레시피 댓글 수정")
    @AuthenticatedUser
    @PutMapping("/{commentId}")
    public ResponseDto<RecipeCommentResponseDto> update(
        @RequestBody RecipeCommentRequestDto requestDto,
        @PathVariable("commentId") Long commentId,
        @PathVariable String recipeId) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeCommentService.update(commentId, userId, requestDto));
    }

    @AuditLog(action = AuditActionType.RECIPE_COMMENT_DELETE)
    @Operation(summary = "레시피 댓글 삭제")
    @AuthenticatedUser
    @DeleteMapping("/{commentId}")
    public ResponseDto<Void> delete(
        @PathVariable("commentId") Long commentId,
        @PathVariable String recipeId) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        recipeCommentService.delete(commentId, userId);
        return ResponseDto.of(null);
    }

    @Operation(summary = "레시피 댓글 Count 조회")
    @GetMapping("/count")
    public ResponseDto<Integer> count(@PathVariable("recipeId") Long recipeId) {
        return ResponseDto.of(recipeCommentService.countCommentsByRecipe(recipeId));
    }
}
