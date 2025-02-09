package com.mars.app.domain.user_recipe.dto.like;

import lombok.Builder;

@Builder
public record UserRecipeLikeMessageDto(
    Long userRecipeId,
    Long userId
) {
    public static UserRecipeLikeMessageDto of(Long userRecipeId, Long userId) {
        return UserRecipeLikeMessageDto.builder()
            .userRecipeId(userRecipeId)
            .userId(userId)
            .build();
    }
}
