package com.mars.app.domain.recipe.messaging;

import static com.mars.app.config.rabbitmq.RabbitMQConfig.LIKE_QUEUE_NAME;
import static com.mars.common.exception.NPGExceptionType.SERVER_ERROR_RABBITMQ_CONNECTION;

import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.app.domain.recipe.service.RecipeLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeNotificationConsumer {

    private final RecipeLikeService recipeLikeService;
    private final SseEmitterService sseEmitterService;

    @RabbitListener(queues = LIKE_QUEUE_NAME)
    public void processLikeEvent(RecipeLikeResponseDto notification) {
        try {
            processLikeNotification(notification);
        } catch (AmqpConnectException e) {
            rabbitMQConnectionError(e);
        }
    }

    private void processLikeNotification(RecipeLikeResponseDto notification) {
        int likeCount = recipeLikeService.getLikeCount(notification.recipeId());
        sseEmitterService.sendLikeCount(notification.recipeId(), likeCount);
    }

    private void rabbitMQConnectionError(AmqpConnectException e) {
        throw SERVER_ERROR_RABBITMQ_CONNECTION.of("RabbitMQ 연결에 실패했습니다.");
    }
}
