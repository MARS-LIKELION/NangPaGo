package com.mars.app.domain.user_recipe.event;

import com.mars.app.domain.user_recipe.dto.like.UserRecipeLikeSseDto;
import com.mars.common.sse.AbstractSseEmitterService;
import org.springframework.stereotype.Service;

@Service
public class UserRecipeLikeSseService extends AbstractSseEmitterService<UserRecipeLikeSseDto> {

    private static final String USER_RECIPE_LIKE_EVENT_NAME = "USER_RECIPE_LIKE_EVENT";

    @Override
    protected String getEventName() {
        return USER_RECIPE_LIKE_EVENT_NAME;
    }
}
