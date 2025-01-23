package com.mars.app.domain.recipe.dto;

import lombok.Builder;

@Builder
public record RecipeLikeMessageDto(
    Long recipeId,
    String email
) {
    public static RecipeLikeMessageDto of(Long recipeId, String email) {
        return RecipeLikeMessageDto.builder()
            .recipeId(recipeId)
            .email(email)
            .build();
    }
}
