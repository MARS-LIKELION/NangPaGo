package com.mars.app.domain.comment.recipe.controller;

import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.comment.recipe.dto.RecipeCommentRequestDto;
import com.mars.app.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.app.domain.comment.recipe.service.RecipeCommentService;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.enums.audit.AuditActionType;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.mars.app.aop.audit.AuditLog;

@RequiredArgsConstructor
@Tag(name = "레시피 댓글 API", description = "레시피 댓글 관련 API")
@RequestMapping("/api/recipe/{recipeId}/comment")
@RestController
public class RecipeCommentController {

    private final RecipeCommentService recipeCommentService;

    @GetMapping
    public ResponseDto<PageResponseDto<RecipeCommentResponseDto>> list(
        @PathVariable("recipeId") Long recipeId, PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeCommentService.pagedCommentsByRecipe(recipeId, userId, pageRequestVO));
    }

    @AuditLog(
        action = AuditActionType.RECIPE_COMMENT_CREATE,
        dtoType = RecipeCommentRequestDto.class
    )
    @AuthenticatedUser
    @PostMapping
    public ResponseDto<RecipeCommentResponseDto> create(
        @RequestBody RecipeCommentRequestDto requestDto,
        @PathVariable("recipeId") Long recipeId) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeCommentService.create(requestDto, userId, recipeId));
    }

    @AuditLog(
        action = AuditActionType.RECIPE_COMMENT_UPDATE,
        dtoType = RecipeCommentRequestDto.class
    )
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
    @AuthenticatedUser
    @DeleteMapping("/{commentId}")
    public ResponseDto<Void> delete(
        @PathVariable("commentId") Long commentId,
        @PathVariable String recipeId) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        recipeCommentService.delete(commentId, userId);
        return ResponseDto.of(null);
    }

    @GetMapping("/count")
    public ResponseDto<Integer> count(@PathVariable("recipeId") Long recipeId) {
        return ResponseDto.of(recipeCommentService.countCommentsByRecipe(recipeId));
    }
}
