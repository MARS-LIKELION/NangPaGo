package com.mars.NangPaGo.domain.comment.recipe.dto;

import com.mars.NangPaGo.domain.comment.recipe.entity.RecipeComment;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RecipeCommentResponseDto(
    Long id,
    String content,
    String userEmail,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static RecipeCommentResponseDto from(RecipeComment recipeComment) {
        return RecipeCommentResponseDto.builder()
            .id(recipeComment.getId())
            .content(recipeComment.getContent())
            .userEmail(recipeComment.getUser().getEmail())
            .createdAt(recipeComment.getCreatedAt())
            .updatedAt(recipeComment.getUpdatedAt())
            .build();
    }
}
