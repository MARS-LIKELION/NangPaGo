package com.mars.app.domain.recipe.event;

import com.mars.common.sse.AbstractSseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeLikeEventListener {

    private final AbstractSseEmitterService<Integer> recipeLikeSseService;

    @EventListener
    public void handleRecipeLikeEvent(RecipeLikeEvent event) {
        recipeLikeSseService.sendToClient(event.getRecipeId(), event.getLikeCount());
    }
}
