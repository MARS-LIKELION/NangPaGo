package com.mars.NangPaGo.domain.community.dto;

import lombok.Builder;

@Builder
public record CommunityLikeResponseDto(
    Long communityId,
    boolean liked
) {
    public static CommunityLikeResponseDto of(Long communityId, boolean liked) {
        return CommunityLikeResponseDto.builder()
            .communityId(communityId)
            .liked(liked)
            .build();
    }
}
