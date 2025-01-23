package com.mars.app.domain.recipe.dto;

import lombok.Builder;

@Builder
public record RecipeLikeResponseDto(
    Long recipeId
) {
    public static RecipeLikeResponseDto of(Long recipeId) {
        return RecipeLikeResponseDto.builder()
            .recipeId(recipeId)
            .build();
    }
}
