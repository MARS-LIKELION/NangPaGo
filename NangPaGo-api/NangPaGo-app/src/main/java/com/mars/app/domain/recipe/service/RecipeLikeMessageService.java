package com.mars.app.domain.recipe.service;

import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.app.domain.recipe.messaging.LikeNotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecipeLikeMessageService {

    private final LikeNotificationPublisher likeNotificationPublisher;

    public RecipeLikeResponseDto toggleLike(Long recipeId, String email) {
        // Message 전송 to RabbitMQ
        likeNotificationPublisher.sendLikeNotification(recipeId, email);

        return RecipeLikeResponseDto.of(recipeId);
    }

}
