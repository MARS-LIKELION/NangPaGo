package com.mars.app.domain.recipe.dto;

import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.recipe.RecipeLike;
import lombok.Builder;

import java.util.List;

@Builder
public record RecipeListResponseDto(
    String id,
    String title,
    String mainImageUrl,
    List<String> ingredientsDisplayTag,
    int likeCount,
    int commentCount,
    boolean isLiked
) {
    public static RecipeListResponseDto from(Recipe recipe) {

        return RecipeListResponseDto.builder()
            .id(recipe.getId().toString())
            .title(recipe.getName())
            .mainImageUrl(recipe.getMainImage())
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

    public static RecipeListResponseDto of(Recipe recipe,
        int likeCount,
        int commentCount,
        List<RecipeLike> recipeLikesByUserId) {

        return RecipeListResponseDto.builder()
            .id(recipe.getId().toString())
            .title(recipe.getName())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isLiked(recipeLikesByUserId.stream()
                .anyMatch(recipeLike -> recipe.getId().equals(recipeLike.getRecipe().getId())))
            .mainImageUrl(recipe.getMainImage())
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
