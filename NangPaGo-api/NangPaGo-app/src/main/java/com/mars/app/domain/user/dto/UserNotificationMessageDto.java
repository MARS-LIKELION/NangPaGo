package com.mars.app.domain.user.dto;

import com.mars.common.enums.user.UserNotificationEventCode;
import com.mars.common.model.user.UserNotification;
import lombok.Builder;

@Builder
public record UserNotificationMessageDto(
    UserNotificationEventCode userNotificationEventCode,
    Long senderId,
    Long postId
) {
    public static UserNotificationMessageDto of(UserNotificationEventCode userNotificationEventCode,
        Long senderId,
        Long postId) {
        return UserNotificationMessageDto.builder()
            .userNotificationEventCode(userNotificationEventCode)
            .senderId(senderId)
            .postId(postId)
            .build();
    }

    public UserNotification toEntity() {
        return UserNotification.builder()
            .userNotificationEventCode(this.userNotificationEventCode)
            .senderId(this.senderId)
            .postId(this.postId)
            .build();
    }
}

