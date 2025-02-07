package com.mars.app.domain.recipe.dto.favorite;

import lombok.Builder;

@Builder
public record RecipeFavoriteMessageDto(
    Long recipeId,
    Long userId
) {
    public static RecipeFavoriteMessageDto of(Long recipeId, Long userId) {
        return RecipeFavoriteMessageDto.builder()
            .recipeId(recipeId)
            .userId(userId)
            .build();
    }
}
