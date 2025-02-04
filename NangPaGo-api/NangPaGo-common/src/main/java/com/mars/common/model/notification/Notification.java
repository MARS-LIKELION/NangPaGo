package com.mars.common.model.notification;

import com.mars.common.enums.event.EventCode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Timestamp;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;
    private Long postId;
    private Long commentId;

    @Enumerated(EnumType.STRING)
    private EventCode eventCode;

    @ColumnDefault("false")
    private Boolean isRead;

    @CreationTimestamp
    private Timestamp createdAt;
}
