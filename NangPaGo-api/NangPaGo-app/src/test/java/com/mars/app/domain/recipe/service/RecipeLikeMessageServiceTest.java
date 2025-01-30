package com.mars.app.domain.recipe.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.app.domain.recipe.messaging.LikeNotificationPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeLikeMessageServiceTest {

    @Mock
    private LikeNotificationPublisher likeNotificationPublisher;

    @InjectMocks
    private RecipeLikeMessageService recipeLikeMessageService;

    @DisplayName("좋아요 토글 메시지를 RabbitMQ로 전송할 수 있다.")
    @Test
    void toggleLike() {
        // given
        Long recipeId = 1L;
        Long userId = 1L;

        // when
        RecipeLikeResponseDto response = recipeLikeMessageService.toggleLike(recipeId, userId);

        // then
        verify(likeNotificationPublisher).sendLikeNotification(recipeId, userId);
        assertThat(response.recipeId()).isEqualTo(recipeId);
    }

    @DisplayName("좋아요 토글 응답은 레시피 ID를 포함한다.")
    @Test
    void toggleLikeResponse() {
        // given
        Long recipeId = 1L;
        Long userId = 1L;

        // when
        RecipeLikeResponseDto response = recipeLikeMessageService.toggleLike(recipeId, userId);

        // then
        assertThat(response)
            .isNotNull()
            .extracting(RecipeLikeResponseDto::recipeId)
            .isEqualTo(recipeId);
    }
}
