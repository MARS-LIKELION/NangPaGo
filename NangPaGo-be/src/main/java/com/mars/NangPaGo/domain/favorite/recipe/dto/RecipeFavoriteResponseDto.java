package com.mars.NangPaGo.domain.favorite.recipe.dto;

import lombok.Builder;

@Builder
public record RecipeFavoriteResponseDto(
    Long recipeId,
    boolean favorited
) {
    public static RecipeFavoriteResponseDto of(Long recipeId, boolean favorited) {
        return RecipeFavoriteResponseDto.builder()
            .recipeId(recipeId)
            .favorited(favorited)
            .build();
    }
}
