package com.mars.common.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;

public interface RabbitMQConfig {

    /**
     * 반드시 `name =` 속성(중복되지 않게 유의)을 포함한 {@link Bean} 어노테이션을 추가해야 합니다.
     * @return 생성된 Queue 객체
     */
    Queue queue();

    /**
     * 반드시 `name =` 속성(중복되지 않게 유의)을 포함한 {@link Bean} 어노테이션을 추가해야 합니다.
     * @param exchange 바인딩할 TopicExchange
     * @return 생성된 Binding 객체
     */
    Binding binding(TopicExchange exchange);

    String getRoutingKey();
    String getQueueName();
}
