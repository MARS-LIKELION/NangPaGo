package com.mars.app.domain.user_recipe.dto;

import com.mars.common.model.userRecipe.UserRecipe;
import com.mars.common.model.userRecipe.UserRecipeLike;
import java.util.List;
import lombok.Builder;

@Builder
public record UserRecipeListResponseDto(
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

    public static UserRecipeListResponseDto of(UserRecipe userRecipe,
        int likeCount,
        int commentCount,
        List<UserRecipeLike> userRecipeLikesByUserId) {

        return UserRecipeListResponseDto.builder()
            .id(userRecipe.getId().toString())
            .title(userRecipe.getTitle())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isLiked(userRecipeLikesByUserId.stream()
                .anyMatch(userRecipeLike -> userRecipe.getId().equals(userRecipeLike.getUserRecipe().getId())))
            .mainImageUrl(getImageUrlOrDefault(userRecipe.getMainImageUrl()))
            .content(getPreviewContent(userRecipe.getContent(), 150))
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

