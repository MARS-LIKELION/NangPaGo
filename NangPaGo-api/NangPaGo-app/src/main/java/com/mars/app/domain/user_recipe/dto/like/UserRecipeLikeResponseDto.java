package com.mars.app.domain.user_recipe.dto.like;

import lombok.Builder;

@Builder
public record UserRecipeLikeResponseDto(
    Long userRecipeId
) {
    public static UserRecipeLikeResponseDto of(Long userRecipeId) {
        return UserRecipeLikeResponseDto.builder()
            .userRecipeId(userRecipeId)
            .build();
    }
}
