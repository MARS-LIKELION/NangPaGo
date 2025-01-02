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
    public static RecipeEsResponseDto of(RecipeEs recipeES, float score) {
        return RecipeEsResponseDto.builder()
            .id(recipeES.getId())
            .name(recipeES.getName())
            .recipeImageUrl(recipeES.getRecipeImageUrl())
            .ingredients(recipeES.getIngredients())
            .ingredientsTag(recipeES.getIngredientsTag())
            .ingredientsDisplayTag(recipeES.getIngredientsDisplayTag())
            .matchScore(score)
            .build();
    }
}
