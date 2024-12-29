package com.mars.NangPaGo.domain.comment.recipe.controller;

import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentRequestDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.NangPaGo.domain.comment.recipe.service.RecipeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe/{recipeId}/comments")
@RequiredArgsConstructor
public class RecipeCommentController {

    private final RecipeCommentService recipeCommentService;

    @GetMapping
    public ResponseDto<List<RecipeCommentResponseDto>> list(@PathVariable("recipeId") Long recipeId) {
        return ResponseDto.of(recipeCommentService.commentsByRecipe(recipeId), "댓글 목록 조회 성공");
    }

    @PostMapping
    public ResponseDto<RecipeCommentResponseDto> create(
        @RequestBody RecipeCommentRequestDto requestDto,
        @PathVariable("recipeId") Long recipeId) {
        return ResponseDto.of(recipeCommentService.create(requestDto, recipeId), "댓글 생성 성공");
    }

    @PutMapping("/{commentId}")
    public ResponseDto<RecipeCommentResponseDto> update(
        @RequestBody RecipeCommentRequestDto requestDto,
        @PathVariable("recipeId") Long recipeId,
        @PathVariable("commentId") Long commentId) {
        return ResponseDto.of(recipeCommentService.update(commentId, requestDto), "댓글 수정 성공");
    }

    @DeleteMapping("/{commentId}")
    public ResponseDto<Void> delete(
        @PathVariable("recipeId") Long recipeId,
        @PathVariable("commentId") Long commentId) {
        recipeCommentService.delete(commentId);
        return ResponseDto.of(null, "댓글 삭제 성공");
    }
}
