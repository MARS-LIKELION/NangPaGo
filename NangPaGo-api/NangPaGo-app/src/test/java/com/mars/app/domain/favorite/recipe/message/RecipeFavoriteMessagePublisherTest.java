package com.mars.app.domain.favorite.recipe.message;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mars.app.config.rabbitmq.impl.RecipeFavoriteRabbitConfig;
import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteMessageDto;
import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteResponseDto;
import com.mars.app.domain.recipe.message.favorite.RecipeFavoriteMessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class RecipeFavoriteMessagePublisherTest {

    private static final String RECIPE_FAVORITE_ROUTING_KEY = "recipe.favorite.*";

    @Mock
    private TopicExchange topicExchange;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private RecipeFavoriteRabbitConfig rabbitConfig;

    @InjectMocks
    private RecipeFavoriteMessagePublisher recipeFavoriteMessagePublisher;

    @BeforeEach
    void setUp() {
        when(rabbitConfig.getRoutingKey()).thenReturn(RECIPE_FAVORITE_ROUTING_KEY);
    }

    @DisplayName("레시피 즐겨찾기 토글 이벤트를 발행할 수 있다.")
    @Test
    void toggleFavorite() {
        // given
        Long recipeId = 1L;
        Long userId = 1L;
        String exchangeName = "test-exchange";

        when(topicExchange.getName()).thenReturn(exchangeName);

        // when
        RecipeFavoriteResponseDto result = recipeFavoriteMessagePublisher.toggleFavorite(recipeId, userId);

        // then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq(RECIPE_FAVORITE_ROUTING_KEY),
            any(RecipeFavoriteMessageDto.class)
        );

        assertThat(result)
            .extracting("recipeId")
            .isEqualTo(recipeId);
    }

    @DisplayName("레시피 즐겨찾기 메시지 발행 시 올바른 DTO가 전송되는지 확인한다.")
    @Test
    void verifyMessageDto() {
        // given
        Long recipeId = 1L;
        Long userId = 1L;
        String exchangeName = "test-exchange";

        when(topicExchange.getName()).thenReturn(exchangeName);

        // when
        recipeFavoriteMessagePublisher.toggleFavorite(recipeId, userId);

        // then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq(RECIPE_FAVORITE_ROUTING_KEY),
            eq(RecipeFavoriteMessageDto.of(recipeId, userId))
        );
    }

}
