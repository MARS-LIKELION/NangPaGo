package com.mars.app.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${RABBITMQ_CONSUMER_NAME}")
    private String developer;
    public static final String RECIPE_LIKE_ROUTING_KEY = "recipe.like.*";
    public static final String RECIPE_FAVORITE_ROUTING_KEY = "recipe.favorite.*";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(this.getExchangeName());
    }

    @Bean(name = "recipeLikeQueue")
    public Queue recipeLikeQueue() {
        return new Queue(this.getLikeQueueName());
    }

    @Bean(name = "recipeFavoriteQueue")
    public Queue recipeFavoriteQueue() {
        return new Queue(this.getFavoriteQueueName());
    }

    @Bean
    public Binding recipeLikeBinding(@Qualifier("recipeLikeQueue") Queue recipeLikeQueue, TopicExchange exchange) {
        return BindingBuilder.bind(recipeLikeQueue).to(exchange).with(RECIPE_LIKE_ROUTING_KEY);
    }

    @Bean
    public Binding recipeFavoriteBinding(@Qualifier("recipeFavoriteQueue") Queue recipeFavoriteQueue, TopicExchange exchange) {
        return BindingBuilder.bind(recipeFavoriteQueue).to(exchange).with(RECIPE_FAVORITE_ROUTING_KEY);
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
        return "recipe-like-notification-queue-" + developer;
    }

    private String getFavoriteQueueName() {
        return "recipe-favorite-notification-queue-" + developer;
    }

    private String getExchangeName() {
        return "notification-exchange-" + developer;
    }
}
