package com.mars.app.domain.recipe.dto;

import com.mars.common.model.recipe.RecipeEs;

public record RecipeSearchResponseDto(
    String id,
    String name,
    String highlightedName
) {
    public static RecipeSearchResponseDto of(RecipeEs recipeEs, String highlightedName) {
        return new RecipeSearchResponseDto(
            recipeEs.getId(),
            recipeEs.getName(),
            highlightedName
        );
    }
}
