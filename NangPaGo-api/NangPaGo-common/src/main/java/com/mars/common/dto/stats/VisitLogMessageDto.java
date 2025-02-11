package com.mars.common.dto.stats;

import com.mars.common.model.stats.VisitLog;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VisitLogMessageDto(
    Long userId,
    String ip,
    LocalDateTime timestamp
) {
    public static VisitLogMessageDto of(Long userId, String ip, LocalDateTime timestamp) {
        return VisitLogMessageDto.builder()
            .userId(userId)
            .ip(ip)
            .timestamp(timestamp)
            .build();
    }

    public VisitLog toVisitLog() {
        return VisitLog.of(userId, ip, timestamp);
    }
}
