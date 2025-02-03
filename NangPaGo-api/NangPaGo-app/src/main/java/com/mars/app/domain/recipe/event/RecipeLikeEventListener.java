package com.mars.app.domain.recipe.event;

import com.mars.app.domain.recipe.dto.RecipeLikeSseDto;
import com.mars.common.sse.AbstractSseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipeLikeEventListener {

    private final AbstractSseEmitterService<RecipeLikeSseDto> recipeLikeSseService;

    @EventListener
    public void handleRecipeLikeEvent(RecipeLikeEvent event) {
        RecipeLikeSseDto recipeLikeSseDto = RecipeLikeSseDto.of(event.getUserId(), event.getLikeCount());
        recipeLikeSseService.sendToClient(event.getRecipeId(), recipeLikeSseDto);
    }
}
