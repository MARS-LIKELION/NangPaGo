package com.mars.app.domain.community.event;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommunityLikeEvent extends ApplicationEvent {
    private final Long communityId;
    private final Long userId;
    private final int likeCount;

    @Builder
    private CommunityLikeEvent(Object source, Long communityId, Long userId, int likeCount) {
        super(source);
        this.communityId = communityId;
        this.userId = userId;
        this.likeCount = likeCount;
    }

    public static CommunityLikeEvent of(Object source, Long communityId, Long userId, int likeCount) {
        return CommunityLikeEvent.builder()
            .source(source)
            .communityId(communityId)
            .userId(userId)
            .likeCount(likeCount)
            .build();
    }
}
