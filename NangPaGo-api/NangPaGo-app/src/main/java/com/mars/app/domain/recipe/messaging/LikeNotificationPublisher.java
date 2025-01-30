package com.mars.app.domain.recipe.messaging;

import static com.mars.app.config.rabbitmq.RabbitMQConfig.LIKE_ROUTING_KEY;

import com.mars.app.domain.recipe.dto.RecipeLikeMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeNotificationPublisher {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;

    public void sendLikeNotification(Long recipeId, Long userId) {
        RecipeLikeMessageDto recipeLikeMessageDto = RecipeLikeMessageDto.of(recipeId, userId);

        rabbitTemplate.convertAndSend(topicExchange.getName(), LIKE_ROUTING_KEY, recipeLikeMessageDto);
    }
}
