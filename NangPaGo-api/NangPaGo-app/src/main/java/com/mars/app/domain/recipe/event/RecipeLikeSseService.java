package com.mars.app.domain.recipe.event;

import com.mars.app.domain.recipe.dto.like.RecipeLikeSseDto;
import com.mars.common.sse.AbstractSseEmitterService;
import org.springframework.stereotype.Service;

@Service
public class RecipeLikeSseService extends AbstractSseEmitterService<RecipeLikeSseDto> {

    private static final String RECIPE_LIKE_EVENT_NAME = "RECIPE_LIKE_EVENT";

    @Override
    protected String getEventName() {
        return RECIPE_LIKE_EVENT_NAME;
    }
}
