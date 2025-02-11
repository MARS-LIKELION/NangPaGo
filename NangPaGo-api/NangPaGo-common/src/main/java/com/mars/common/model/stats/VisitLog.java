package com.mars.common.model.stats;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document(collection = "visit_logs")
public class VisitLog {

    @Id
    private String id;

    private Long userId;
    private String ip;
    private LocalDateTime timestamp;

    @Builder
    private VisitLog(Long userId, String ip, LocalDateTime timestamp) {
        this.userId = userId;
        this.ip = ip;
        this.timestamp = timestamp;
    }

    public static VisitLog of(Long userId, String ip, LocalDateTime timestamp) {
        return VisitLog.builder()
            .userId(userId)
            .ip(ip)
            .timestamp(timestamp)
            .build();
    }
}
