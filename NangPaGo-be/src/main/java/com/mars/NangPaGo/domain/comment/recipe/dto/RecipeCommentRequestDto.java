package com.mars.NangPaGo.domain.comment.recipe.dto;

public record RecipeCommentRequestDto(
    Long recipeId,
    String userEmail,
    String content
) {
}

