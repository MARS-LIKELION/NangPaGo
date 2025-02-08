package com.mars.app.domain.community.dto.like;

import lombok.Builder;

@Builder
public record CommunityLikeResponseDto(
    Long communityId
) {
    public static CommunityLikeResponseDto of(Long communityId) {
        return CommunityLikeResponseDto.builder()
            .communityId(communityId)
            .build();
    }
}
