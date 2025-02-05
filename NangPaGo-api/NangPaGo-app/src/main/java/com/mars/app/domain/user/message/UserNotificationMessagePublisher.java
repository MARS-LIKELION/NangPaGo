package com.mars.app.domain.user.message;

import com.mars.app.config.rabbitmq.impl.UserNotificationRabbitConfig;
import com.mars.app.domain.user.dto.UserNotificationMessageDto;
import com.mars.common.enums.user.UserNotificationEventCode;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserNotificationMessagePublisher {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;
    private final UserNotificationRabbitConfig rabbitConfig;

    public void createUserNotification(UserNotificationEventCode userNotificationEventCode,
        Long senderId,
        Long postId) {

        UserNotificationMessageDto userNotificationMessageDto = UserNotificationMessageDto.of(
            userNotificationEventCode,
            senderId,
            postId
        );

        rabbitTemplate.convertAndSend(topicExchange.getName(), rabbitConfig.getRoutingKey(), userNotificationMessageDto);
    }
}
