package com.mars.NangPaGo.domain.community.dto;

import com.mars.NangPaGo.domain.community.entity.Community;
import com.mars.NangPaGo.domain.user.entity.User;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommunityResponseDto(
    Long id,
    String title,
    String content,
    String imageUrl,
    String email,
    boolean isOwnedByUser,
    boolean isPublic,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static final String DEFAULT_IMAGE_URL =
        "https://storage.googleapis.com/nangpago-9d371.firebasestorage.app/dc137676-6240-4920-97d3-727c4b7d6d8d_360_F_517535712_q7f9QC9X6TQxWi6xYZZbMmw5cnLMr279.jpg";

    public static CommunityResponseDto of(Community community, String email) {
        return CommunityResponseDto.builder()
            .id(community.getId())
            .title(community.getTitle())
            .content(community.getContent())
            .imageUrl(getImageUrlOrDefault(community.getImageUrl()))
            .email(maskEmail(community.getUser().getEmail()))
            .isOwnedByUser(community.getUser().getEmail().equals(email))
            .isPublic(community.isPublic())
            .createdAt(community.getCreatedAt())
            .updatedAt(community.getUpdatedAt())
            .build();
    }

    private static String getImageUrlOrDefault(String imageUrl) {
        return (imageUrl == null || imageUrl.isBlank()) ? DEFAULT_IMAGE_URL : imageUrl;
    }

    private static String maskEmail(String email) {
        if (email.indexOf("@") <= 3) {
            return email.replaceAll("(?<=.).(?=.*@)", "*");
        }
        return email.replaceAll("(?<=.{3}).(?=.*@)", "*");
    }
}
