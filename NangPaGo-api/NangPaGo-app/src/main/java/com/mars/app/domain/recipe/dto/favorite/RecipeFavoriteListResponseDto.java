package com.mars.app.domain.recipe.dto.favorite;

import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.recipe.RecipeLike;
import lombok.Builder;

import java.util.List;

@Builder
public record RecipeFavoriteListResponseDto(
    String id,
    String name,
    int likeCount,
    int commentCount,
    boolean isLiked,
    String recipeImageUrl,
    List<String> ingredientsDisplayTag
) {
    public static RecipeFavoriteListResponseDto of(Recipe recipe,
        int likeCount,
        int commentCount,
        List<RecipeLike> recipeLikesByUserId) {

        return RecipeFavoriteListResponseDto.builder()
            .id(recipe.getId().toString())
            .name(recipe.getName())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isLiked(recipeLikesByUserId.stream()
                .anyMatch(recipeLike -> recipe.getId().equals(recipeLike.getRecipe().getId())))
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
