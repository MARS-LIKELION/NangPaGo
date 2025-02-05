package com.mars.app.domain.audit.message;

import com.mars.app.config.rabbitmq.impl.AuditLogRabbitConfig;
import com.mars.app.domain.audit.dto.AuditLogMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuditLogMessagePublisher {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;
    private final AuditLogRabbitConfig rabbitConfig;

    public void createAuditLog(String action, String userId, String requestDto, String arguments) {
        AuditLogMessageDto auditLogMessageDto = AuditLogMessageDto.of(action, userId, requestDto, arguments);
        rabbitTemplate.convertAndSend(topicExchange.getName(), rabbitConfig.getRoutingKey(), auditLogMessageDto);
    }
}
