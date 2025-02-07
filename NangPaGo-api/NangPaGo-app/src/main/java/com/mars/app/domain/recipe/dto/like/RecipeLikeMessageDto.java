package com.mars.app.domain.recipe.dto.like;

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
