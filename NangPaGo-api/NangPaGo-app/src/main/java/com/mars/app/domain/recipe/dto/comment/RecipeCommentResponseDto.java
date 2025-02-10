package com.mars.app.domain.recipe.dto.comment;

import com.mars.common.model.comment.recipe.RecipeComment;
import com.mars.common.model.recipe.Recipe;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RecipeCommentResponseDto(
    Long id,
    Long postId,
    String content,
    String writerName,
    boolean isOwnedByUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static RecipeCommentResponseDto from(RecipeComment recipeComment, Recipe recipe, Long userId) {
        return RecipeCommentResponseDto.builder()
            .id(recipeComment.getId())
            .postId(recipeComment.getRecipe().getId())
            .content(recipeComment.getContent())
            .writerName(recipeComment.getUser().getNickname())
            .isOwnedByUser(recipeComment.getUser().getId().equals(userId))
            .createdAt(recipeComment.getCreatedAt())
            .updatedAt(recipeComment.getUpdatedAt())
            .build();
    }
}
