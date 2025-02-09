package com.mars.app.domain.user.dto;

import com.mars.common.model.user.UserNotification;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserNotificationResponseDto(
    boolean isRead,
    Long postId,
    String postType,
    LocalDateTime timestamp
) {
    public static UserNotificationResponseDto from(UserNotification userNotification) {
        return UserNotificationResponseDto.builder()
            .isRead(userNotification.getIsRead())
            .postId(userNotification.getPostId())
            .postType(userNotification.getPostType())
            .timestamp(userNotification.getTimestamp())
            .build();
    }
}
