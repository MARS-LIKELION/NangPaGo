package com.mars.app.domain.recipe.message.like;

import com.mars.app.config.rabbitmq.impl.RecipeLikeRabbitConfig;
import com.mars.app.domain.recipe.dto.like.RecipeLikeMessageDto;
import com.mars.app.domain.recipe.dto.like.RecipeLikeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecipeLikeMessagePublisher {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;
    private final RecipeLikeRabbitConfig rabbitConfig;

    public RecipeLikeResponseDto toggleLike(Long recipeId, Long userId) {
        sendLikeNotification(recipeId, userId);

        return RecipeLikeResponseDto.of(recipeId);
    }

    private void sendLikeNotification(Long recipeId, Long userId) {
        // Message 전송 to RabbitMQ
        RecipeLikeMessageDto recipeLikeMessageDto = RecipeLikeMessageDto.of(recipeId, userId);
        rabbitTemplate.convertAndSend(topicExchange.getName(), rabbitConfig.getRoutingKey(), recipeLikeMessageDto);
    }
}
