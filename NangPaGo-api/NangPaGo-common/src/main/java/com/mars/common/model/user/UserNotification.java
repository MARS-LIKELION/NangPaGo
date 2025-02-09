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
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_notification")
public class UserNotification {

    @Id
    private String id;

    @NotNull
    private String userNotificationEventCode;

    @NotNull
    private Long userId;
    @NotNull
    private Long senderId;

    private String postType;
    private Long postId;

    private Boolean isRead;
    private LocalDateTime timestamp;

    @Builder
    private UserNotification(UserNotificationEventCode userNotificationEventCode, Long userId, Long senderId, Long postId, LocalDateTime timestamp) {
        this.userNotificationEventCode = userNotificationEventCode.getCode();
        this.userId = userId;
        this.senderId = senderId;
        this.postType = userNotificationEventCode.getPostType();
        this.postId = postId;
        this.isRead = false;
        this.timestamp = timestamp;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
