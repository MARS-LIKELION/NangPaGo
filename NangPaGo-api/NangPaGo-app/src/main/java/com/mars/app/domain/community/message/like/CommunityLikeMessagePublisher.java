package com.mars.app.domain.community.message.like;

import com.mars.app.config.rabbitmq.impl.CommunityLikeRabbitConfig;
import com.mars.app.domain.community.dto.like.CommunityLikeMessageDto;
import com.mars.app.domain.community.dto.like.CommunityLikeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommunityLikeMessagePublisher {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;
    private final CommunityLikeRabbitConfig rabbitConfig;

    public CommunityLikeResponseDto toggleLike(Long communityId, Long userId) {
        sendLikeNotification(communityId, userId);
        return CommunityLikeResponseDto.of(communityId);
    }

    private void sendLikeNotification(Long communityId, Long userId) {
        CommunityLikeMessageDto communityLikeMessageDto = CommunityLikeMessageDto.of(communityId, userId);
        rabbitTemplate.convertAndSend(topicExchange.getName(), rabbitConfig.getRoutingKey(), communityLikeMessageDto);
    }
}
