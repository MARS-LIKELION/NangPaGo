package com.mars.app.domain.audit.message;

import com.mars.app.domain.audit.dto.AuditLogMessageDto;
import com.mars.app.domain.audit.repository.AuditLogRepository;
import com.mars.common.model.audit.AuditLog;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuditLogMessageConsumer {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    @RabbitListener(queues = "#{@auditLogQueue.name}")
    public void processAuditLogMessage(AuditLogMessageDto auditLogMessageDto) {
        AuditLog auditLog = AuditLog.builder()
            .action(auditLogMessageDto.action())
            .userId(auditLogMessageDto.userId())
            .requestDto(auditLogMessageDto.requestDto())
            .arguments(auditLogMessageDto.arguments())
            .build();

        auditLogRepository.save(auditLog);
    }
}
