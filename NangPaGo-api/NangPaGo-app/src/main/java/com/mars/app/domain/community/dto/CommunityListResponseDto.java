package com.mars.app.domain.community.dto;

import com.mars.common.model.community.Community;
import com.mars.common.model.community.CommunityLike;
import java.util.List;
import lombok.Builder;

@Builder
public record CommunityListResponseDto(
    String id,
    String title,
    String content,
    String mainImageUrl,
    int likeCount,
    int commentCount,
    boolean isLiked
) {
    public static final String DEFAULT_IMAGE_URL =
        "https://storage.googleapis.com/nangpago-9d371.firebasestorage.app/dc137676-6240-4920-97d3-727c4b7d6d8d_360_F_517535712_q7f9QC9X6TQxWi6xYZZbMmw5cnLMr279.jpg";

    public static CommunityListResponseDto from(Community community) {

        return CommunityListResponseDto.builder()
            .id(community.getId().toString())
            .title(community.getTitle())
            .mainImageUrl(getImageUrlOrDefault(community.getImageUrl()))
            .content(getPreviewContent(community.getContent(), 250))
            .build();
    }

    public static CommunityListResponseDto of(Community community,
        int likeCount,
        int commentCount,
        List<CommunityLike> communityLikesByUserId) {

        return CommunityListResponseDto.builder()
            .id(community.getId().toString())
            .title(community.getTitle())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isLiked(communityLikesByUserId.stream()
                .anyMatch(communityLike -> community.getId().equals(communityLike.getCommunity().getId())))
            .mainImageUrl(getImageUrlOrDefault(community.getImageUrl()))
            .content(getPreviewContent(community.getContent(), 150))
            .build();
    }

    private static String getImageUrlOrDefault(String imageUrl) {
        return (imageUrl == null || imageUrl.isBlank()) ? DEFAULT_IMAGE_URL : imageUrl;
    }

    private static String getPreviewContent(String content, int maxLength) {
        if (content.length() > maxLength) {
            return content.substring(0, maxLength);
        }
        return content;
    }
}

