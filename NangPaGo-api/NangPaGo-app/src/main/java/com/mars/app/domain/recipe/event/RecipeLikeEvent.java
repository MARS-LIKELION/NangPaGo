package com.mars.app.domain.recipe.event;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RecipeLikeEvent extends ApplicationEvent {
    private final Long recipeId;
    private final Long userId;
    private final int likeCount;

    @Builder
    private RecipeLikeEvent(Object source, Long recipeId, Long userId, int likeCount) {
        super(source);
        this.recipeId = recipeId;
        this.userId = userId;
        this.likeCount = likeCount;
    }

    public static RecipeLikeEvent of(Object source, Long recipeId, Long userId, int likeCount) {
        return RecipeLikeEvent.builder()
            .source(source)
            .recipeId(recipeId)
            .userId(userId)
            .likeCount(likeCount)
            .build();
    }
}
