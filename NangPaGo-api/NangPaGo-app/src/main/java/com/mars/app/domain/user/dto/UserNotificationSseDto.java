package com.mars.app.domain.user.dto;

import lombok.Builder;

@Builder
public record UserNotificationSseDto(
    Long senderId,
    Long postId
) {
    public static UserNotificationSseDto of(Long senderId, Long postId) {
        return UserNotificationSseDto.builder()
            .senderId(senderId)
            .postId(postId)
            .build();
    }
}
