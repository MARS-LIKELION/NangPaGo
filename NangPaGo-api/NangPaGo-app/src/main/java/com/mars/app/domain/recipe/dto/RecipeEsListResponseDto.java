package com.mars.app.domain.recipe.dto;

import com.mars.common.model.recipe.RecipeEs;
import com.mars.common.model.recipe.RecipeLike;
import lombok.Builder;
import java.util.List;

@Builder
public record RecipeEsListResponseDto(
    String id,
    String title,
    String highlightedName,
    String mainImageUrl,
    List<String> ingredientsDisplayTag,
    int likeCount,
    int commentCount,
    boolean isLiked
) {

    public static RecipeEsListResponseDto of(RecipeEs recipeEs,
        String highlightedName,
        int likeCount,
        int commentCount,
        List<RecipeLike> recipeLikesByUserId
    ) {

        return RecipeEsListResponseDto.builder()
            .id(recipeEs.getId())
            .title(recipeEs.getName())
            .highlightedName(highlightedName)
            .mainImageUrl(recipeEs.getRecipeImageUrl())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .isLiked(recipeLikesByUserId.stream()
                .anyMatch(recipeLike -> recipeEs.getId().equals(String.valueOf(recipeLike.getRecipe().getId()))))
            .ingredientsDisplayTag(recipeEs.getIngredientsDisplayTag())
            .build();
    }
}
