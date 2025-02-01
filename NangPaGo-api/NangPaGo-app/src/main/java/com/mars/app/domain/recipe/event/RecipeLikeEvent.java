package com.mars.app.domain.recipe.event;

public record RecipeLikeEvent(
    Long recipeId,
    Long userId,
    int likeCount
) {
    public static RecipeLikeEvent of(Long recipeId, Long userId, int likeCount) {
        return new RecipeLikeEvent(recipeId, userId, likeCount);
    }
}
