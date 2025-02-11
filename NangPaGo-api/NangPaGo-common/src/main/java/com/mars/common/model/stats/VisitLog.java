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
    private VisitLog(Long userId, String ip) {
        this.userId = userId;
        this.ip = ip;
        this.timestamp = LocalDateTime.now();
    }

    public static VisitLog of(Long userId, String ip) {
        return VisitLog.builder()
            .userId(userId)
            .ip(ip)
            .build();
    }
}
