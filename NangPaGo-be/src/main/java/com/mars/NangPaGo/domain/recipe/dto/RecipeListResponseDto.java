package com.mars.NangPaGo.domain.recipe.dto;

import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import lombok.Builder;

@Builder
public record RecipeListResponseDto(
        String name,
        String ingredients, //조리방법
        String category,
        Integer calories,
        String hashTag,
        String mainImage
) {
    public static RecipeListResponseDto toDto(Recipe recipe) {
        return RecipeListResponseDto.builder()
                .name(recipe.getName())
                .ingredients(recipe.getIngredients())
                .category(recipe.getCategory())
                .calories(recipe.getCalories())
                .hashTag(recipe.getHashTag())
                .mainImage(recipe.getMainImage())
                .build();
    }
}