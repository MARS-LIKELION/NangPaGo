package com.mars.app.domain.user_recipe.dto;

import com.mars.common.model.userRecipe.UserRecipe;
import java.time.LocalDateTime;
import java.util.List;

import com.mars.common.model.userRecipe.UserRecipeIngredient;
import com.mars.common.model.userRecipe.UserRecipeManual;
import lombok.Builder;

@Builder
public record UserRecipeResponseDto(
    Long id,
    String title,
    String content,
    String mainImageUrl,
    String email,
    int likeCount,
    int commentCount,
    boolean isOwnedByUser,
    boolean isPublic,
    String recipeStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<IngredientDto> ingredients,
    List<ManualDto> manuals
) {
    public static final String DEFAULT_IMAGE_URL =
        "https://storage.googleapis.com/nangpago-9d371.firebasestorage.app/dc137676-6240-4920-97d3-727c4b7d6d8d_360_F_517535712_q7f9QC9X6TQxWi6xYZZbMmw5cnLMr279.jpg";

    public static UserRecipeResponseDto of(UserRecipe userRecipe, int likeCount, int commentCount, Long userId) {
        return UserRecipeResponseDto.builder()
            .id(userRecipe.getId())
            .title(userRecipe.getTitle())
            .content(userRecipe.getContent())
            .mainImageUrl(getImageUrlOrDefault(userRecipe.getMainImageUrl()))
            .email(maskEmail(userRecipe.getUser().getEmail()))
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isOwnedByUser(userRecipe.getUser().getId().equals(userId))
            .isPublic(userRecipe.isPublic())
            .recipeStatus(userRecipe.getRecipeStatus().name())
            .createdAt(userRecipe.getCreatedAt())
            .updatedAt(userRecipe.getUpdatedAt())
            .ingredients(userRecipe.getIngredients().stream()
                .map(IngredientDto::of)
                .toList())
            .manuals(userRecipe.getManuals().stream()
                .map(ManualDto::of)
                .toList())
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

    @Builder
    public record ManualDto(
        int step,
        String description,
        String imageUrl
    ){
        public static ManualDto of(UserRecipeManual manual){
            return ManualDto.builder()
               .step(manual.getStep())
               .description(manual.getDescription())
               .imageUrl(getImageUrlOrDefault(manual.getImageUrl()))
               .build();
        }
    }

    @Builder
    public record IngredientDto(
        String name,
        String amount
    ){
        public static IngredientDto of(UserRecipeIngredient ingredient){
            return IngredientDto.builder()
               .name(ingredient.getName())
               .amount(ingredient.getAmount())
               .build();
        }
    }
}
