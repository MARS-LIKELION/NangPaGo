package com.mars.NangPaGo.domain.favorite.recipe.dto;

import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import lombok.Builder;

import java.util.List;

@Builder
public record RecipeFavoriteListResponseDto(
    String id,
    String name,
    String recipeImageUrl,
    List<String> ingredients,
    List<String> ingredientsTag,
    List<String> ingredientsDisplayTag
) {
    public static RecipeFavoriteListResponseDto of(Recipe recipe) {
        return RecipeFavoriteListResponseDto.builder()
            .id(recipe.getId().toString())
            .name(recipe.getName())
            .recipeImageUrl(recipe.getMainImage())
            .ingredients(List.of(recipe.getIngredients().split(",")))
            .ingredientsTag(List.of(recipe.getIngredients().split(",")))
            .ingredientsDisplayTag(List.of(
                    recipe.getMainIngredient(),
                    recipe.getCalorie() != null ? recipe.getCalorie() + " kcal" : null,
                    recipe.getCategory(),
                    recipe.getCookingMethod()
                ).stream()
                .filter(tag -> tag != null && !tag.isEmpty())
                .toList())
            .build();
    }
}
