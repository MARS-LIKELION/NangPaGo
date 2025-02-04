package com.mars.app.domain.notification.dto;

import com.mars.common.enums.event.EventCode;
import com.mars.common.model.notification.Notification;
import lombok.Builder;

@Builder
public record NotificationMessage(
    Long senderId,
    Long receiverId,
    String postType,
    Long postId,
    EventCode eventCode
) {
    public static NotificationMessage from(Notification notification) {
        return NotificationMessage.builder()
            .senderId(notification.getSenderId())
            .receiverId(notification.getReceiverId())
            .postType(notification.getPostType())
            .postId(notification.getPostId())
            .eventCode(notification.getEventCode())
            .build();
    }
}

