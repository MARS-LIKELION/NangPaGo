package com.mars.app.domain.userRecipe.dto;

import lombok.Builder;

@Builder
public record UserRecipeLikeResponseDto(
    Long id,
    boolean liked
) {
    public static UserRecipeLikeResponseDto of(Long id, boolean liked) {
        return UserRecipeLikeResponseDto.builder()
            .id(id)
            .liked(liked)
            .build();
    }
}
