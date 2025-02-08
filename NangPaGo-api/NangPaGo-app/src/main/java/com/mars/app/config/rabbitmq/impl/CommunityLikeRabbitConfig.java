package com.mars.app.config.rabbitmq.impl;

import com.mars.app.config.rabbitmq.RabbitMQCommonConfig;
import com.mars.app.config.rabbitmq.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class CommunityLikeRabbitConfig implements RabbitMQConfig {

    private static final String ROUTING_KEY = "community.like.*";
    private final RabbitMQCommonConfig rabbitMQCommonConfig;

    @Override
    @Bean(name = "communityLikeQueue")
    public Queue queue() {
        return new Queue(getQueueName());
    }

    @Override
    @Bean(name = "communityLikeKeyBinding")
    public Binding binding(TopicExchange exchange) {
        return BindingBuilder.bind(queue()).to(exchange).with(getRoutingKey());
    }

    @Override
    public String getRoutingKey() {
        return ROUTING_KEY;
    }

    @Override
    public String getQueueName() {
        return "community-like-notification-queue-" + rabbitMQCommonConfig.getDeveloper();
    }
}
