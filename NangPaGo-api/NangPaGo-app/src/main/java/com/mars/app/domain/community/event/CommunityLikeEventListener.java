package com.mars.app.domain.community.event;

import com.mars.app.domain.community.dto.like.CommunityLikeSseDto;
import com.mars.common.sse.AbstractSseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommunityLikeEventListener {

    private final AbstractSseEmitterService<CommunityLikeSseDto> communityLikeSseService;

    @EventListener
    public void handleCommunityLikeEvent(CommunityLikeEvent event) {
        CommunityLikeSseDto communityLikeSseDto = CommunityLikeSseDto.of(event.getUserId(), event.getLikeCount());
        communityLikeSseService.sendToClient(event.getCommunityId(), communityLikeSseDto);
    }
}
