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
public class UserRecipeLikeRabbitConfig implements RabbitMQConfig {

    private static final String ROUTING_KEY = "user.recipe.like.*";
    private final RabbitMQCommonConfig rabbitMQCommonConfig;

    @Override
    @Bean(name = "userRecipeLikeQueue")
    public Queue queue() {
        return new Queue(getQueueName());
    }

    @Override
    @Bean(name = "userRecipeLikeKeyBinding")
    public Binding binding(TopicExchange exchange) {
        return BindingBuilder.bind(queue()).to(exchange).with(getRoutingKey());
    }

    @Override
    public String getRoutingKey() {
        return ROUTING_KEY;
    }

    @Override
    public String getQueueName() {
        return "user-recipe-like-notification-queue-" + rabbitMQCommonConfig.getDeveloper();
    }
}
