package com.mars.app.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${RABBITMQ_CONSUMER_NAME}")
    private String developer;
    public static final String LIKE_ROUTING_KEY = "recipe.like.*";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(this.getExchangeName());
    }

    @Bean
    public Queue likeQueue() {
        return new Queue(this.getLikeQueueName());
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(LIKE_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    private String getLikeQueueName() {
        return "like-notification-queue-" + developer;
    }

    private String getExchangeName() {
        return "notification-exchange-" + developer;
    }
}
