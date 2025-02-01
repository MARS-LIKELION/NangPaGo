package com.mars.app.domain.recipe.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mars.app.config.rabbitmq.RabbitMQConfig;
import com.mars.app.domain.recipe.dto.RecipeLikeMessageDto;
import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class RecipeLikeNotificationPublisherTest {

    @Mock
    private TopicExchange topicExchange;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RecipeLikeNotificationPublisher recipeLikeNotificationPublisher;

    @DisplayName("레시피 좋아요 토글 이벤트를 발행할 수 있다.")
    @Test
    void toggleLike() {
        // given
        Long recipeId = 1L;
        Long userId = 1L;
        String exchangeName = "test-exchange";

        when(topicExchange.getName()).thenReturn(exchangeName);

        // when
        RecipeLikeResponseDto result = recipeLikeNotificationPublisher.toggleLike(recipeId, userId);

        // then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq(RabbitMQConfig.LIKE_ROUTING_KEY),
            any(RecipeLikeMessageDto.class)
        );

        assertThat(result)
            .extracting("recipeId")
            .isEqualTo(recipeId);
    }

    @DisplayName("메시지 발행 시 올바른 DTO가 전송되는지 확인한다.")
    @Test
    void verifyMessageDto() {
        // given
        Long recipeId = 1L;
        Long userId = 1L;
        String exchangeName = "test-exchange";

        when(topicExchange.getName()).thenReturn(exchangeName);

        // when
        recipeLikeNotificationPublisher.toggleLike(recipeId, userId);

        // then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq(RabbitMQConfig.LIKE_ROUTING_KEY),
            eq(RecipeLikeMessageDto.of(recipeId, userId))
        );
    }
}
