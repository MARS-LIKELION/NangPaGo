package com.mars.app.domain.recipe.dto;

import lombok.Builder;

@Builder
public record RecipeLikeMessageDto(
    Long recipeId,
    Long userId
) {
    public static RecipeLikeMessageDto of(Long recipeId, Long userId) {
        return RecipeLikeMessageDto.builder()
            .recipeId(recipeId)
            .userId(userId)
            .build();
    }
}
