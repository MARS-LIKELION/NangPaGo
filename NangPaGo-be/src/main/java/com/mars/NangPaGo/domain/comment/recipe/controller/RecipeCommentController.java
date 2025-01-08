package com.mars.NangPaGo.domain.comment.recipe.controller;

import com.mars.NangPaGo.common.dto.PageDto;
import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.common.aop.auth.AuthenticatedUser;
import com.mars.NangPaGo.common.component.auth.AuthenticationHolder;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentRequestDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.NangPaGo.domain.comment.recipe.service.RecipeCommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "레시피 댓글 API", description = "레시피 댓글 관련 API")
@RequestMapping("/api/recipe/{recipeId}/comments")
@RestController
public class RecipeCommentController {

    private final RecipeCommentService recipeCommentService;

    @GetMapping
    public ResponseDto<PageDto<RecipeCommentResponseDto>> list(
        @PathVariable("recipeId") Long recipeId,
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "5") int pageSize) {

        String email = AuthenticationHolder.getCurrentUserEmail();

        return ResponseDto.of(recipeCommentService.pagedCommentsByRecipe(recipeId, email, pageNo, pageSize));
    }

    @AuthenticatedUser
    @PostMapping
    public ResponseDto<RecipeCommentResponseDto> create(
        @RequestBody RecipeCommentRequestDto requestDto,
        @PathVariable("recipeId") Long recipeId) {

        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(recipeCommentService.create(requestDto, email, recipeId));
    }

    @AuthenticatedUser
    @PutMapping("/{commentId}")
    public ResponseDto<RecipeCommentResponseDto> update(
        @RequestBody RecipeCommentRequestDto requestDto,
        @PathVariable("commentId") Long commentId,
        @PathVariable String recipeId) {

        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(recipeCommentService.update(commentId, email, requestDto));
    }

    @AuthenticatedUser
    @DeleteMapping("/{commentId}")
    public ResponseDto<Void> delete(
        @PathVariable("commentId") Long commentId,
        @PathVariable String recipeId) {

        String email = AuthenticationHolder.getCurrentUserEmail();
        recipeCommentService.delete(commentId, email);
        return ResponseDto.of(null);
    }

    @GetMapping("/count")
    public ResponseDto<Integer> count(@PathVariable("recipeId") Long recipeId) {
        return ResponseDto.of(recipeCommentService.countCommentsByRecipe(recipeId));
    }
}
