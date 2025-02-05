package com.mars.app.config.rabbitmq;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@RequiredArgsConstructor
@Configuration
public class RabbitMQCommonConfig {

    @Value("${RABBITMQ_CONSUMER_NAME}")
    private String developer;

    private final Jackson2JsonMessageConverter jsonMessageConverter;

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(this.getExchangeName());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    private String getExchangeName() {
        return "notification-exchange-" + developer;
    }
}
