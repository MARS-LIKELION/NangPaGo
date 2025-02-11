package com.mars.app.domain.stats.message;

import com.mars.common.config.rabbitmq.impl.VisitLogRabbitConfig;
import com.mars.common.dto.stats.VisitLogMessageDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VisitLogMessagePublisher {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;
    private final VisitLogRabbitConfig rabbitConfig;

    public void saveVisitLog(Long userId, String ip, LocalDateTime timestamp) {
        VisitLogMessageDto messageDto = VisitLogMessageDto.of(userId, ip, timestamp);
        rabbitTemplate.convertAndSend(topicExchange.getName(), rabbitConfig.getRoutingKey(), messageDto);
    }
}
