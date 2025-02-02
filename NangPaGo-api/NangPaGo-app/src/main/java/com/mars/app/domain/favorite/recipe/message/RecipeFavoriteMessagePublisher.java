package com.mars.app.domain.favorite.recipe.message;

import static com.mars.app.config.rabbitmq.RabbitMQConfig.*;

import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteMessageDto;
import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecipeFavoriteMessagePublisher {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;

    public RecipeFavoriteResponseDto toggleFavorite(Long recipeId, Long userId) {
        sendFavoriteMessage(recipeId, userId);
        return RecipeFavoriteResponseDto.of(recipeId);
    }

    private void sendFavoriteMessage(Long recipeId, Long userId) {
        RecipeFavoriteMessageDto recipeFavoriteMessageDto = RecipeFavoriteMessageDto.of(recipeId, userId);
        rabbitTemplate.convertAndSend(topicExchange.getName(), RECIPE_FAVORITE_ROUTING_KEY, recipeFavoriteMessageDto);
    }
}
