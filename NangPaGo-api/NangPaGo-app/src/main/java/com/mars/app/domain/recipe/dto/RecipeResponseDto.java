package com.mars.app.domain.recipe.dto;

import com.mars.common.model.recipe.Manual;
import com.mars.common.model.recipe.ManualImage;
import com.mars.common.model.recipe.Recipe;
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
    Integer calorie,
    Integer fat,
    Integer carbohydrate,
    Integer protein,
    Integer natrium,
    String mainIngredient,
    String mainImage,
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
            .calorie(recipe.getCalorie())
            .fat(recipe.getFat())
            .carbohydrate(recipe.getCarbohydrate())
            .protein(recipe.getProtein())
            .natrium(recipe.getNatrium())
            .mainIngredient(recipe.getMainIngredient())
            .mainImage(recipe.getMainImage())
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
