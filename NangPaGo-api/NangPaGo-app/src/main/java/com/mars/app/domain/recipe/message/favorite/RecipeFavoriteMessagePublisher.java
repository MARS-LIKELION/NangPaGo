package com.mars.app.domain.recipe.message.favorite;

import com.mars.app.config.rabbitmq.impl.RecipeFavoriteRabbitConfig;
import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteMessageDto;
import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecipeFavoriteMessagePublisher {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;
    private final RecipeFavoriteRabbitConfig rabbitConfig;

    public RecipeFavoriteResponseDto toggleFavorite(Long recipeId, Long userId) {
        sendFavoriteMessage(recipeId, userId);
        return RecipeFavoriteResponseDto.of(recipeId);
    }

    private void sendFavoriteMessage(Long recipeId, Long userId) {
        RecipeFavoriteMessageDto recipeFavoriteMessageDto = RecipeFavoriteMessageDto.of(recipeId, userId);
        rabbitTemplate.convertAndSend(topicExchange.getName(), rabbitConfig.getRoutingKey(), recipeFavoriteMessageDto);
    }
}
