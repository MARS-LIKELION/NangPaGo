package com.mars.app.domain.user_recipe.event;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRecipeLikeEvent extends ApplicationEvent {
    private final Long userRecipeId;
    private final Long userId;
    private final int likeCount;

    @Builder
    private UserRecipeLikeEvent(Object source, Long userRecipeId, Long userId, int likeCount) {
        super(source);
        this.userRecipeId = userRecipeId;
        this.userId = userId;
        this.likeCount = likeCount;
    }

    public static UserRecipeLikeEvent of(Object source, Long userRecipeId, Long userId, int likeCount) {
        return UserRecipeLikeEvent.builder()
            .source(source)
            .userRecipeId(userRecipeId)
            .userId(userId)
            .likeCount(likeCount)
            .build();
    }
}
