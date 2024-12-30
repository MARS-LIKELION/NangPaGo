package com.mars.NangPaGo.domain.comment.recipe.controller;

import com.mars.NangPaGo.common.dto.PageDto;
import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentRequestDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.NangPaGo.domain.comment.recipe.service.RecipeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipe/{recipeId}/comments")
@RequiredArgsConstructor
public class RecipeCommentController {

    private final RecipeCommentService recipeCommentService;

    @GetMapping
    public ResponseDto<PageDto<RecipeCommentResponseDto>> list(
        @PathVariable("recipeId") Long recipeId,
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "5") int pageSize) {
        return ResponseDto.of(recipeCommentService.PagedCommentsByRecipe(recipeId, pageNo, pageSize), "댓글 조회");
    }

    @PostMapping
    public ResponseDto<RecipeCommentResponseDto> create(
        @RequestBody RecipeCommentRequestDto requestDto,
        @PathVariable("recipeId") Long recipeId) {
        return ResponseDto.of(recipeCommentService.create(requestDto, recipeId), "댓글 생성");
    }

    @PutMapping("/{commentId}")
    public ResponseDto<RecipeCommentResponseDto> update(
        @RequestBody RecipeCommentRequestDto requestDto,
        @PathVariable("recipeId") Long recipeId,
        @PathVariable("commentId") Long commentId) {
        return ResponseDto.of(recipeCommentService.update(commentId, requestDto), "댓글 수정");
    }

    @DeleteMapping("/{commentId}")
    public ResponseDto<Void> delete(
        @PathVariable("recipeId") Long recipeId,
        @PathVariable("commentId") Long commentId) {
        recipeCommentService.delete(commentId);
        return ResponseDto.of(null, "댓글 삭제");
    }
}
