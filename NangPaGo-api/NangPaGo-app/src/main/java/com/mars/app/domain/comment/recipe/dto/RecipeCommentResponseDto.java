package com.mars.app.domain.comment.recipe.dto;

import com.mars.common.model.comment.recipe.RecipeComment;
import com.mars.common.model.recipe.Recipe;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RecipeCommentResponseDto(
    Long id,
    Long postId,
    String content,
    String imageUrl,
    String title,
    String email,
    boolean isOwnedByUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static RecipeCommentResponseDto from(RecipeComment recipeComment, Recipe recipe, Long userId) {
        return RecipeCommentResponseDto.builder()
            .id(recipeComment.getId())
            .postId(recipeComment.getRecipe().getId())
            .content(recipeComment.getContent())
            .imageUrl(recipe.getMainImage())
            .title(recipe.getName())
            .email(maskEmail(recipeComment.getUser().getEmail()))
            .isOwnedByUser(recipeComment.getUser().getId().equals(userId))
            .createdAt(recipeComment.getCreatedAt())
            .updatedAt(recipeComment.getUpdatedAt())
            .build();
    }

    private static String maskEmail(String email) {
        if (email.indexOf("@") <= 3) {
            return email.replaceAll("(?<=.).(?=.*@)", "*");
        }
        return email.replaceAll("(?<=.{3}).(?=.*@)", "*");
    }
}
