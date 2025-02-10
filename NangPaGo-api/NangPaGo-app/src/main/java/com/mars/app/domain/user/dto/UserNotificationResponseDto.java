package com.mars.app.domain.user.dto;

import com.mars.common.model.user.UserNotification;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserNotificationResponseDto(
    boolean isRead,
    Long postId,
    String senderNickname,
    String eventCode,
    LocalDateTime timestamp
) {
    public static UserNotificationResponseDto from(UserNotification userNotification) {
        return UserNotificationResponseDto.builder()
            .isRead(userNotification.getIsRead())
            .postId(userNotification.getPostId())
            .senderNickname(userNotification.getSenderNickname())
            .eventCode(userNotification.getUserNotificationEventCode())
            .timestamp(userNotification.getTimestamp())
            .build();
    }
}
