package com.mars.app.domain.community.dto.like;

import lombok.Builder;

@Builder
public record CommunityLikeMessageDto(
    Long communityId,
    Long userId
) {
    public static CommunityLikeMessageDto of(Long communityId, Long userId) {
        return CommunityLikeMessageDto.builder()
            .communityId(communityId)
            .userId(userId)
            .build();
    }
}
