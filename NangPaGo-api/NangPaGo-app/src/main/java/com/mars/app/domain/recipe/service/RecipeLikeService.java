package com.mars.app.domain.recipe.service;

import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.app.domain.recipe.messaging.LikeNotificationPublisher;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeLikeService {

    private final RecipeLikeRepository recipeLikeRepository;
    private final LikeNotificationPublisher likeNotificationPublisher;

    public boolean isLiked(Long recipeId, String email) {
        return recipeLikeRepository.findByEmailAndRecipeId(email, recipeId).isPresent();
    }

    public int getLikeCount(Long recipeId) {
        return recipeLikeRepository.countByRecipeId(recipeId);
    }

    public RecipeLikeResponseDto toggleLike(Long recipeId, String email) {
        // Message 전송 to RabbitMQ
        likeNotificationPublisher.sendLikeNotification(recipeId, email);

        return RecipeLikeResponseDto.of(recipeId);
    }
}
