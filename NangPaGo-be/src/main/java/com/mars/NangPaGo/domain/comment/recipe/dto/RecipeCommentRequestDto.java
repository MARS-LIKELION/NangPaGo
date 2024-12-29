package com.mars.NangPaGo.domain.comment.recipe.dto;

public record RecipeCommentRequestDto(
    Long recipeId,
    Long userId,
    String content
) {
}

