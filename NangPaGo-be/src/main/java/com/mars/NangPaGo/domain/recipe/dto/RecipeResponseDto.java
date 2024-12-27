package com.mars.NangPaGo.domain.recipe.dto;

import com.mars.NangPaGo.domain.recipe.entity.Manual;
import com.mars.NangPaGo.domain.recipe.entity.ManualImage;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record RecipeResponseDto(
    Long id,
    String name,
    String ingredients,
    String cookingMethod,
    String category,
    Integer calories,
    Integer fat,
    Integer carbohydrates,
    Integer protein,
    Integer sodium,
    String hashTag,
    String mainImage,
    String stepImage,
    List<ManualDto> manuals,
    List<ManualImageDto> manualImages
) {
    public static RecipeResponseDto from(Recipe recipe) {
        return RecipeResponseDto.builder()
            .id(recipe.getId())
            .name(recipe.getName())
            .ingredients(recipe.getIngredients())
            .cookingMethod(recipe.getCookingMethod())
            .category(recipe.getCategory())
            .calories(recipe.getCalories())
            .fat(recipe.getFat())
            .carbohydrates(recipe.getCarbohydrates())
            .protein(recipe.getProtein())
            .sodium(recipe.getSodium())
            .hashTag(recipe.getHashTag())
            .mainImage(recipe.getMainImage())
            .stepImage(recipe.getStepImage())
            .manuals(recipe.getManuals().stream().map(ManualDto::from).collect(Collectors.toList()))
            .manualImages(recipe.getManualImages().stream().map(ManualImageDto::from).collect(Collectors.toList()))
            .build();
    }

    @Builder
    record ManualDto(
        Long id,
        String manual
    ) {
        public static ManualDto from(Manual manual) {
            return ManualDto.builder()
                .id(manual.getId())
                .manual(manual.getManual())
                .build();
        }
    }

    @Builder
    record ManualImageDto(
        Long id,
        String imageUrl
    ) {
        public static ManualImageDto from(ManualImage manualImage) {
            return ManualImageDto.builder()
                .id(manualImage.getId())
                .imageUrl(manualImage.getImageUrl())
                .build();
        }
    }
}
