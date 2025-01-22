package com.mars.app.domain.recipe.messaging;

import static com.mars.app.config.rabbitmq.RabbitMQConfig.EXCHANGE_NAME;
import static com.mars.app.config.rabbitmq.RabbitMQConfig.LIKE_ROUTING_KEY;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;

@RequiredArgsConstructor
@Service
public class LikeNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendLikeNotification(RecipeLikeResponseDto notification) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, LIKE_ROUTING_KEY,  notification);
    }
}
