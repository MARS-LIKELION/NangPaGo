package com.mars.NangPaGo.domain.community.dto;

import lombok.Builder;

@Builder
public record CommunityLikeResponseDto(
    Long id,
    boolean liked
) {
    public static CommunityLikeResponseDto of(Long id, boolean liked) {
        return CommunityLikeResponseDto.builder()
            .id(id)
            .liked(liked)
            .build();
    }
}
