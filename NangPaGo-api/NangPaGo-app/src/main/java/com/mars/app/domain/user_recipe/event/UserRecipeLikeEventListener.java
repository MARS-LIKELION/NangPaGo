package com.mars.app.domain.user_recipe.event;

import com.mars.app.domain.user_recipe.dto.like.UserRecipeLikeSseDto;
import com.mars.common.sse.AbstractSseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserRecipeLikeEventListener {

    private final AbstractSseEmitterService<UserRecipeLikeSseDto> userRecipeLikeSseService;

    @EventListener
    public void handleUserRecipeLikeEvent(UserRecipeLikeEvent event) {
        UserRecipeLikeSseDto userRecipeLikeSseDto = UserRecipeLikeSseDto.of(event.getUserId(), event.getLikeCount());
        userRecipeLikeSseService.sendToClient(event.getUserRecipeId(), userRecipeLikeSseDto);
    }
}
