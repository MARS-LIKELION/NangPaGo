package com.mars.app.domain.user.dto;

import lombok.Builder;

@Builder
public record UserNotificationSseDto(
    Long senderId,
    Long postId,
    String message
) {
    public static UserNotificationSseDto of(Long senderId, Long postId, String message) {
        return UserNotificationSseDto.builder()
            .senderId(senderId)
            .postId(postId)
            .message(message)
            .build();
    }
}
