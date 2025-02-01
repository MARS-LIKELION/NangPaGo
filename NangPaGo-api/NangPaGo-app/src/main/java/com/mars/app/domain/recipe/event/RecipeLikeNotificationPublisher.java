package com.mars.app.domain.recipe.event;

import static com.mars.app.config.rabbitmq.RabbitMQConfig.LIKE_ROUTING_KEY;

import com.mars.app.domain.recipe.dto.RecipeLikeMessageDto;
import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecipeLikeNotificationPublisher {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;

    public RecipeLikeResponseDto toggleLike(Long recipeId, Long userId) {
        sendLikeNotification(recipeId, userId);

        return RecipeLikeResponseDto.of(recipeId);
    }

    private void sendLikeNotification(Long recipeId, Long userId) {
        // Message 전송 to RabbitMQ
        RecipeLikeMessageDto recipeLikeMessageDto = RecipeLikeMessageDto.of(recipeId, userId);
        rabbitTemplate.convertAndSend(topicExchange.getName(), LIKE_ROUTING_KEY, recipeLikeMessageDto);
    }
}
