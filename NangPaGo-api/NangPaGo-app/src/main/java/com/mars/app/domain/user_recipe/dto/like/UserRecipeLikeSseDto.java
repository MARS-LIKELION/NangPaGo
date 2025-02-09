package com.mars.app.domain.user_recipe.dto.like;

import lombok.Builder;

@Builder
public record UserRecipeLikeSseDto(
    Long userId,
    int likeCount
) {
    public static UserRecipeLikeSseDto of(Long userId, int likeCount) {
        return UserRecipeLikeSseDto.builder()
            .userId(userId)
            .likeCount(likeCount)
            .build();
    }
}
