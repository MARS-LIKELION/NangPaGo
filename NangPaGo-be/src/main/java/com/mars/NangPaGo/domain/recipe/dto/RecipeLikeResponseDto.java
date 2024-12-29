package com.mars.NangPaGo.domain.recipe.dto;

import lombok.Builder;

@Builder
public record RecipeLikeResponseDto(
    Long recipeId,
    boolean liked
) {
    public static RecipeLikeResponseDto of(Long recipeId, boolean liked) {
        return RecipeLikeResponseDto.builder()
            .recipeId(recipeId)
            .liked(liked)
            .build();
    }
}
