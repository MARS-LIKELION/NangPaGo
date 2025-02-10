package com.mars.app.domain.community.dto;

import com.mars.common.model.community.Community;
import com.mars.common.model.community.CommunityLike;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record CommunityResponseDto(
    Long id,
    String title,
    String content,
    String mainImageUrl,
    String nickname,
    int likeCount,
    int commentCount,
    boolean isLiked,
    boolean isOwnedByUser,
    boolean isPublic,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static final String DEFAULT_IMAGE_URL =
        "https://storage.googleapis.com/nangpago-9d371.firebasestorage.app/dc137676-6240-4920-97d3-727c4b7d6d8d_360_F_517535712_q7f9QC9X6TQxWi6xYZZbMmw5cnLMr279.jpg";

    public static CommunityResponseDto of(Community community, Long userId) {
        return CommunityResponseDto.builder()
            .id(community.getId())
            .title(community.getTitle())
            .content(community.getContent())
            .mainImageUrl(getImageUrlOrDefault(community.getImageUrl()))
            .nickname(community.getUser().getNickname())
            .isOwnedByUser(community.getUser().getId().equals(userId))
            .isPublic(community.isPublic())
            .createdAt(community.getCreatedAt())
            .updatedAt(community.getUpdatedAt())
            .build();
    }

    public static CommunityResponseDto of(
        Community community,
        int likeCount,
        int commentCount,
        Long userId,
        List<CommunityLike> communityLikes
    ) {
        return CommunityResponseDto.builder()
            .id(community.getId())
            .title(community.getTitle())
            .content(community.getContent())
            .mainImageUrl(getImageUrlOrDefault(community.getImageUrl()))
            .nickname(community.getUser().getNickname())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isLiked(communityLikes.stream()
                .anyMatch(communityLike -> community.getId().equals(communityLike.getCommunity().getId())))
            .isOwnedByUser(community.getUser().getId().equals(userId))
            .isPublic(community.isPublic())
            .createdAt(community.getCreatedAt())
            .updatedAt(community.getUpdatedAt())
            .build();
    }

    private static String getImageUrlOrDefault(String imageUrl) {
        return (imageUrl == null || imageUrl.isBlank()) ? DEFAULT_IMAGE_URL : imageUrl;
    }
}
