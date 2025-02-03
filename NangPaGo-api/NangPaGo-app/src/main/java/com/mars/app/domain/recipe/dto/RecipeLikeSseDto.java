package com.mars.app.domain.recipe.dto;

import lombok.Builder;

@Builder
public record RecipeLikeSseDto(
    Long userId,
    int likeCount
) {
    public static RecipeLikeSseDto of(Long userId, int likeCount) {
        return RecipeLikeSseDto.builder()
            .userId(userId)
            .likeCount(likeCount)
            .build();
    }
}
