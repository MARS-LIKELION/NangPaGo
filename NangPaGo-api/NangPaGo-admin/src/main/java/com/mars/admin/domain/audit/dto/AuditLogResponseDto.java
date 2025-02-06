package com.mars.admin.domain.audit.dto;

import com.mars.common.model.audit.AuditLog;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AuditLogResponseDto(
    String action,
    String userId,
    String requestDto,
    String arguments,
    LocalDateTime timestamp
) {
    public static AuditLogResponseDto from(AuditLog auditLog) {
        return AuditLogResponseDto.builder()
            .action(auditLog.getAction())
            .userId(auditLog.getUserId())
            .requestDto(auditLog.getRequestDto())
            .arguments(auditLog.getArguments())
            .timestamp(auditLog.getTimestamp())
            .build();
    }
}
