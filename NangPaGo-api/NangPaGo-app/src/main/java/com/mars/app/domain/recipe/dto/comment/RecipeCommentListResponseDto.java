package com.mars.app.domain.recipe.dto.comment;

import com.mars.common.model.comment.recipe.RecipeComment;
import com.mars.common.model.recipe.Recipe;
import lombok.Builder;

@Builder
public record RecipeCommentListResponseDto(
    Long id,
    Long postId,
    String content,
    String mainImageUrl,
    String title
//    LocalDateTime createdAt, // TODO: 마이페이지 내 댓글 리스트에서 날짜를 보여줄 지 결정
//    LocalDateTime updatedAt
) {
    public static RecipeCommentListResponseDto of(RecipeComment recipeComment, Recipe recipe) {
        return RecipeCommentListResponseDto.builder()
            .id(recipeComment.getId())
            .postId(recipeComment.getRecipe().getId())
            .content(recipeComment.getContent())
            .mainImageUrl(recipe.getMainImage())
            .title(recipe.getName())
//            .createdAt(recipeComment.getCreatedAt())
//            .updatedAt(recipeComment.getUpdatedAt())
            .build();
    }
}
