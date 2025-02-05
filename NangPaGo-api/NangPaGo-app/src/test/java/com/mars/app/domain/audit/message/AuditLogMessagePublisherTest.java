package com.mars.app.domain.audit.message;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mars.app.config.rabbitmq.impl.AuditLogRabbitConfig;
import com.mars.app.domain.audit.dto.AuditLogMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class AuditLogMessagePublisherTest {

    private static final String AUDIT_LOG_ROUTING_KEY = "audit.log.*";

    @Mock
    private TopicExchange topicExchange;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private AuditLogRabbitConfig rabbitConfig;

    @InjectMocks
    private AuditLogMessagePublisher auditLogMessagePublisher;

    @BeforeEach
    void setUp() {
        when(rabbitConfig.getRoutingKey()).thenReturn(AUDIT_LOG_ROUTING_KEY);
    }

    @DisplayName("감사 로그 생성 이벤트를 발행할 수 있다.")
    @Test
    void createAuditLog() {
        // given
        String action = "TEST_ACTION";
        String userId = "1L";
        String requestDto = "{\"testDtoKey\": \"testDtoValue\"}";
        String arguments = "testParamName1: testParamValue1, testParamName2: testParamValue2";
        String exchangeName = "test-exchange";

        when(topicExchange.getName()).thenReturn(exchangeName);

        // when
        auditLogMessagePublisher.createAuditLog(action, userId, requestDto, arguments);

        // then
        verify(rabbitTemplate).convertAndSend(
            eq(exchangeName),
            eq(AUDIT_LOG_ROUTING_KEY),
            eq(AuditLogMessageDto.of(action, userId, requestDto, arguments))
        );
    }
}
