package com.mars.NangPaGo.domain.recipe.dto;

import com.mars.NangPaGo.domain.recipe.entity.RecipeEs;
import lombok.Builder;
import java.util.List;

@Builder
public record RecipeEsResponseDto(
    String id,
    String name,
    String recipeImageUrl,
    List<String> ingredients,
    List<String> ingredientsTag,
    List<String> ingredientsDisplayTag,
    float matchScore
) {
    public static RecipeEsResponseDto of(RecipeEs recipeEs, float score) {
        return RecipeEsResponseDto.builder()
            .id(recipeEs.getId())
            .name(recipeEs.getName())
            .recipeImageUrl(recipeEs.getRecipeImageUrl())
            .ingredients(recipeEs.getIngredients())
            .ingredientsTag(recipeEs.getIngredientsTag())
            .ingredientsDisplayTag(recipeEs.getIngredientsDisplayTag())
            .matchScore(score)
            .build();
    }
}
