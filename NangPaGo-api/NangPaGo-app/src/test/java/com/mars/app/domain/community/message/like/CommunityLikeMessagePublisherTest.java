package com.mars.app.domain.community.message.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mars.app.config.rabbitmq.impl.CommunityLikeRabbitConfig;
import com.mars.app.domain.community.dto.like.CommunityLikeMessageDto;
import com.mars.app.domain.community.dto.like.CommunityLikeResponseDto;
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
class CommunityLikeMessagePublisherTest {

    private static final String COMMUNITY_LIKE_ROUTING_KEY = "community.like.*";

    @Mock
    private TopicExchange topicExchange;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private CommunityLikeRabbitConfig rabbitConfig;

    @InjectMocks
    private CommunityLikeMessagePublisher communityLikeMessagePublisher;

    @BeforeEach
    void setUp() {
        when(rabbitConfig.getRoutingKey()).thenReturn(COMMUNITY_LIKE_ROUTING_KEY);
    }

    @DisplayName("커뮤니티 게시글 좋아요 토글 이벤트를 발행할 수 있다.")
    @Test
    void toggleLike() {
        // given
        Long communityId = 1L;
        Long userId = 1L;
        String exchangeName = "test-exchange";

        when(topicExchange.getName()).thenReturn(exchangeName);

        // when
        CommunityLikeResponseDto result = communityLikeMessagePublisher.toggleLike(communityId, userId);

        // then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq(COMMUNITY_LIKE_ROUTING_KEY),
            any(CommunityLikeMessageDto.class)
        );

        assertThat(result)
            .extracting("communityId")
            .isEqualTo(communityId);
    }

    @DisplayName("메시지 발행 시 올바른 DTO가 전송되는지 확인한다.")
    @Test
    void verifyMessageDto() {
        // given
        Long communityId = 1L;
        Long userId = 1L;
        String exchangeName = "test-exchange";

        when(topicExchange.getName()).thenReturn(exchangeName);

        // when
        communityLikeMessagePublisher.toggleLike(communityId, userId);

        // then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq(COMMUNITY_LIKE_ROUTING_KEY),
            eq(CommunityLikeMessageDto.of(communityId, userId))
        );
    }

}
