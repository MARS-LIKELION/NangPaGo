package com.mars.app.domain.userCommunity.dto;

import lombok.Builder;

@Builder
public record UserCommunityLikeResponseDto(
    Long id,
    boolean liked
) {
    public static UserCommunityLikeResponseDto of(Long id, boolean liked) {
        return UserCommunityLikeResponseDto.builder()
            .id(id)
            .liked(liked)
            .build();
    }
}
