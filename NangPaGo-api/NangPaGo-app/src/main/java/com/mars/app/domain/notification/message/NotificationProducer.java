package com.mars.app.domain.notification.message;

import static com.mars.app.config.rabbitmq.RabbitMQConfig.NOTIFICATION_EXCHANGE;
import static com.mars.app.config.rabbitmq.RabbitMQConfig.NOTIFICATION_QUEUE;

import com.mars.app.domain.notification.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendNotification(NotificationMessage notificationMessage) {
        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, NOTIFICATION_QUEUE, notificationMessage);
    }
}
