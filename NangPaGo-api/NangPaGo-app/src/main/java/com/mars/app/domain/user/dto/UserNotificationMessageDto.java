package com.mars.app.domain.user.dto;

import com.mars.common.enums.user.UserNotificationEventCode;
import com.mars.common.model.user.UserNotification;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserNotificationMessageDto(
    UserNotificationEventCode userNotificationEventCode,
    Long senderId,
    Long postId,
    LocalDateTime timestamp
) {
    public static UserNotificationMessageDto of(UserNotificationEventCode userNotificationEventCode,
        Long senderId,
        Long postId,
        LocalDateTime timestamp) {
        return UserNotificationMessageDto.builder()
            .userNotificationEventCode(userNotificationEventCode)
            .senderId(senderId)
            .postId(postId)
            .timestamp(timestamp)
            .build();
    }

    public UserNotification toUserNotification(Long receiverId, String senderNickname) {
        return UserNotification.builder()
            .userNotificationEventCode(this.userNotificationEventCode)
            .userId(receiverId)
            .senderId(this.senderId)
            .senderNickname(senderNickname)
            .postId(this.postId)
            .timestamp(this.timestamp)
            .build();
    }
}

