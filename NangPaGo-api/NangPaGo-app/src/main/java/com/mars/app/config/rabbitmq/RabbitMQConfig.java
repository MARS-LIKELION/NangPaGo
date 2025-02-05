package com.mars.app.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

public interface RabbitMQConfig {
    Queue queue();
    Binding binding(TopicExchange exchange);
    String getRoutingKey();
    String getQueueName();
}
