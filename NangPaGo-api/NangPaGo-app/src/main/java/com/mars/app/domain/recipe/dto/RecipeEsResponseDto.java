package com.mars.app.domain.recipe.dto;

import com.mars.common.model.recipe.RecipeEs;
import com.mars.common.model.recipe.RecipeLike;
import lombok.Builder;
import java.util.List;

@Builder
public record RecipeEsResponseDto(
    String id,
    String name,
    String highlightedName,
    String recipeImageUrl,
    int likeCount,
    int commentCount,
    boolean isLiked,
    List<String> ingredients,
    List<String> ingredientsTag,
    List<String> ingredientsDisplayTag,
    float matchScore
) {

    public static RecipeEsResponseDto of(RecipeEs recipeEs,
        String highlightedName,
        int likeCount,
        int commentCount,
        List<RecipeLike> recipeLikesByUserId,
        float score) {

        return RecipeEsResponseDto.builder()
            .id(recipeEs.getId())
            .name(recipeEs.getName())
            .highlightedName(highlightedName)
            .recipeImageUrl(recipeEs.getRecipeImageUrl())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isLiked(recipeLikesByUserId.stream()
                .anyMatch(recipeLike -> recipeEs.getId().equals(String.valueOf(recipeLike.getRecipe().getId()))))
            .ingredients(recipeEs.getIngredients())
            .ingredientsTag(recipeEs.getIngredientsTag())
            .ingredientsDisplayTag(recipeEs.getIngredientsDisplayTag())
            .matchScore(score)
            .build();
    }
}
