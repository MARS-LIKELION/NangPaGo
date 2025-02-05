package com.mars.common.model.user;

import com.mars.common.enums.user.UserNotificationEventCode;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Document(collection = "user_notification")
public class UserNotification {

    @Id
    private String id;

    @NotNull
    private String userNotificationEventCode;

    @NotNull
    private Long senderId;

    private String postType;
    private Long postId;

    private Boolean isRead;
    private LocalDateTime timestamp;

    @Builder
    private UserNotification(UserNotificationEventCode userNotificationEventCode, Long senderId, Long postId) {
        this.userNotificationEventCode = userNotificationEventCode.getCode();
        this.senderId = senderId;
        this.postType = userNotificationEventCode.getPostType();
        this.postId = postId;
        this.isRead = false;
        this.timestamp = LocalDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
