package com.mars.app.domain.community.event;

import com.mars.app.domain.community.dto.like.CommunityLikeSseDto;
import com.mars.common.sse.AbstractSseEmitterService;
import org.springframework.stereotype.Service;

@Service
public class CommunityLikeSseService extends AbstractSseEmitterService<CommunityLikeSseDto> {

    private static final String COMMUNITY_LIKE_EVENT_NAME = "COMMUNITY_LIKE_EVENT";

    @Override
    protected String getEventName() {
        return COMMUNITY_LIKE_EVENT_NAME;
    }
}
