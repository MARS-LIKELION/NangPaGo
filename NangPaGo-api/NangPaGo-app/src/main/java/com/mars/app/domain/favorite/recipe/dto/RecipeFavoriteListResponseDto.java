package com.mars.app.domain.favorite.recipe.dto;

import com.mars.common.model.recipe.Recipe;
import lombok.Builder;

import java.util.List;

@Builder
public record RecipeFavoriteListResponseDto(
    String id,
    String name,
    int likeCount,
    int commentCount,
    String recipeImageUrl,
    List<String> ingredientsDisplayTag
) {
    public static RecipeFavoriteListResponseDto of(Recipe recipe, int likeCount, int commentCount) {
        return RecipeFavoriteListResponseDto.builder()
            .id(recipe.getId().toString())
            .name(recipe.getName())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .recipeImageUrl(recipe.getMainImage())
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
