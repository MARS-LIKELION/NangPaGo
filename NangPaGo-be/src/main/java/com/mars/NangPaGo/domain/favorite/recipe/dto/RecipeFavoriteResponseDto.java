package com.mars.NangPaGo.domain.favorite.recipe.dto;

import lombok.Builder;

@Builder
public record RecipeFavoriteResponseDto(
    Long recipeId,
    boolean isFavorite
) {
    public static RecipeFavoriteResponseDto of(Long recipeId, boolean isFavorite) {
        return RecipeFavoriteResponseDto.builder()
            .recipeId(recipeId)
            .isFavorite(isFavorite)
            .build();
    }
}
