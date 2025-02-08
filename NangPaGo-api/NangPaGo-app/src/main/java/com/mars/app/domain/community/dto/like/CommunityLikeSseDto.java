package com.mars.app.domain.community.dto.like;

import lombok.Builder;

@Builder
public record CommunityLikeSseDto(
    Long userId,
    int likeCount
) {
    public static CommunityLikeSseDto of(Long userId, int likeCount) {
        return CommunityLikeSseDto.builder()
            .userId(userId)
            .likeCount(likeCount)
            .build();
    }
}
