package com.mars.app.config.rabbitmq.impl;

import com.mars.common.config.rabbitmq.RabbitMQCommonConfig;
import com.mars.common.config.rabbitmq.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class UserNotificationRabbitConfig implements RabbitMQConfig {

    private static final String ROUTING_KEY = "user.notification.*";
    private final RabbitMQCommonConfig rabbitMQCommonConfig;

    @Override
    @Bean(name = "userNotificationQueue")
    public Queue queue() {
        return new Queue(getQueueName());
    }

    @Override
    @Bean(name = "userNotificationKeyBinding")
    public Binding binding(TopicExchange exchange) {
        return BindingBuilder.bind(queue()).to(exchange).with(getRoutingKey());
    }

    @Override
    public String getRoutingKey() {
        return ROUTING_KEY;
    }

    @Override
    public String getQueueName() {
        return "user-notification-queue-" + rabbitMQCommonConfig.getDeveloper();
    }
}
