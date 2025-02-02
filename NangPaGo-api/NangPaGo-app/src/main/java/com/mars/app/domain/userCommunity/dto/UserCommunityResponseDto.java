package com.mars.app.domain.userCommunity.dto;

import com.mars.common.model.userCommunity.UserCommunity;
import java.time.LocalDateTime;
import java.util.List;
import com.mars.common.model.userCommunity.UserCommunityManualImage;
import lombok.Builder;

@Builder
public record UserCommunityResponseDto(
    Long id,
    String title,
    String content,
    String mainImageUrl,
    List<String> imageUrls,
    String email,
    int likeCount,
    int commentCount,
    boolean isOwnedByUser,
    boolean isPublic,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<String> ingredients,
    List<String> manuals
) {
    public static final String DEFAULT_IMAGE_URL =
        "https://storage.googleapis.com/nangpago-9d371.firebasestorage.app/dc137676-6240-4920-97d3-727c4b7d6d8d_360_F_517535712_q7f9QC9X6TQxWi6xYZZbMmw5cnLMr279.jpg";

    public static UserCommunityResponseDto of(UserCommunity userCommunity, int likeCount, int commentCount, Long userId) {
        return UserCommunityResponseDto.builder()
            .id(userCommunity.getId())
            .title(userCommunity.getTitle())
            .content(userCommunity.getContent())
            .mainImageUrl(getImageUrlOrDefault(userCommunity.getMainImageUrl()))
            .imageUrls(userCommunity.getManuals().stream()
                .flatMap(manual -> manual.getImages().stream().map(UserCommunityManualImage::getImageUrl))
                .toList())
            .email(maskEmail(userCommunity.getUser().getEmail()))
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isOwnedByUser(userCommunity.getUser().getId().equals(userId))
            .isPublic(userCommunity.isPublic())
            .createdAt(userCommunity.getCreatedAt())
            .updatedAt(userCommunity.getUpdatedAt())
            .ingredients(userCommunity.getIngredients().stream().map(ingredient -> ingredient.getName() + " " + ingredient.getAmount()).toList()) // 재료를 "이름 + 수량" 형태로 변환
            .manuals(userCommunity.getManuals().stream().map(manual -> manual.getStep() + ". " + manual.getDescription()).toList())
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
