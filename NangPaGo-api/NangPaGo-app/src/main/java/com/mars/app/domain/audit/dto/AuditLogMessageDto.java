package com.mars.app.domain.audit.dto;

import lombok.Builder;

@Builder
public record AuditLogMessageDto(
    String action,
    String userId,
    String requestDto,
    String arguments
) {

    public static AuditLogMessageDto of(String action, String userId, String requestDto, String arguments) {
        return AuditLogMessageDto.builder()
            .action(action)
            .userId(userId)
            .requestDto(requestDto)
            .arguments(arguments)
            .build();
    }
}
