package com.mars.app.domain.comment.recipe.dto;

import com.mars.app.domain.comment.recipe.entity.RecipeComment;
import com.mars.app.domain.recipe.entity.Recipe;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RecipeCommentResponseDto(
    Long id,
    String content,
    String imageUrl,
    String title,
    String email,
    boolean isOwnedByUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static RecipeCommentResponseDto from(RecipeComment recipeComment, Recipe recipe, String email) {
        return RecipeCommentResponseDto.builder()
            .id(recipeComment.getId())
            .content(recipeComment.getContent())
            .imageUrl(recipe.getMainImage())
            .title(recipe.getName())
            .email(maskEmail(recipeComment.getUser().getEmail()))
            .isOwnedByUser(recipeComment.getUser().getEmail().equals(email))
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
