package com.mars.admin.domain.audit.dto;

import com.mars.common.enums.audit.AuditActionType;
import com.mars.common.model.audit.AuditLog;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AuditLogResponseDto(
    String action,
    String actionDescription,
    String userId,
    String email,
    String requestDto,
    String arguments,
    LocalDateTime timestamp
) {
    public static AuditLogResponseDto of(AuditLog auditLog, String email) {
        return AuditLogResponseDto.builder()
            .action(auditLog.getAction())
            .actionDescription(AuditActionType.valueOf(auditLog.getAction()).getDescription())
            .userId(auditLog.getUserId())
            .email(email)
            .requestDto(auditLog.getRequestDto())
            .arguments(auditLog.getArguments())
            .timestamp(auditLog.getTimestamp())
            .build();
    }

    public static AuditLogResponseDto of(AuditLog auditLog) {
        return AuditLogResponseDto.of(auditLog, "");
    }
}
