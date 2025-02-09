package com.mars.app.domain.user.event;

import com.mars.common.enums.user.UserNotificationEventCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserNotificationEvent extends ApplicationEvent {
    private final Long senderId;
    private final Long receiverId;
    private final Long postId;
    private final UserNotificationEventCode userNotificationEventCode;

    @Builder
    private UserNotificationEvent(Object source,
        Long senderId,
        Long receiverId,
        Long postId,
        UserNotificationEventCode userNotificationEventCode) {

        super(source);
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.postId = postId;
        this.userNotificationEventCode = userNotificationEventCode;
    }

    public static UserNotificationEvent of(Object source,
        Long senderId,
        Long receiverId,
        Long postId,
        UserNotificationEventCode userNotificationEventCode) {

        return UserNotificationEvent.builder()
            .source(source)
            .senderId(senderId)
            .receiverId(receiverId)
            .postId(postId)
            .userNotificationEventCode(userNotificationEventCode)
            .build();
    }
}
