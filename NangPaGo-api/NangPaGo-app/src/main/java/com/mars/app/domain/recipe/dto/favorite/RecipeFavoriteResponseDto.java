package com.mars.app.domain.recipe.dto.favorite;

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
