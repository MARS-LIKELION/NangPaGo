package com.mars.app.domain.favorite.recipe.dto;

import lombok.Builder;

@Builder
public record RecipeFavoriteResponseDto(
    Long recipeId
) {
    public static RecipeFavoriteResponseDto of(Long recipeId) {
        return RecipeFavoriteResponseDto.builder()
            .recipeId(recipeId)
            .build();
    }
}
