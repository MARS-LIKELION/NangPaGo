package com.mars.app.domain.user_recipe.message;

import com.mars.app.config.rabbitmq.impl.UserRecipeLikeRabbitConfig;
import com.mars.app.domain.user_recipe.dto.like.UserRecipeLikeMessageDto;
import com.mars.app.domain.user_recipe.dto.like.UserRecipeLikeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserRecipeLikeMessagePublisher {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;
    private final UserRecipeLikeRabbitConfig rabbitConfig;

    public UserRecipeLikeResponseDto toggleLike(Long userRecipeId, Long userId) {
        sendLikeNotification(userRecipeId, userId);
        return UserRecipeLikeResponseDto.of(userRecipeId);
    }

    private void sendLikeNotification(Long userRecipeId, Long userId) {
        UserRecipeLikeMessageDto messageDto = UserRecipeLikeMessageDto.of(userRecipeId, userId);
        rabbitTemplate.convertAndSend(topicExchange.getName(), rabbitConfig.getRoutingKey(), messageDto);
    }
}
