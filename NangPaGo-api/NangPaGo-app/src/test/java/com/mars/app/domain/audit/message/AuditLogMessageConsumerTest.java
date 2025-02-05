package com.mars.app.domain.audit.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.mars.app.domain.audit.dto.AuditLogMessageDto;
import com.mars.app.domain.audit.repository.AuditLogRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.model.audit.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(transactionManager = "mongoTransactionManager")
class AuditLogMessageConsumerTest extends IntegrationTestSupport {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogMessageConsumer auditLogMessageConsumer;

    @DisplayName("감사 로그 메시지를 처리하고 저장할 수 있다.")
    @Test
    void processAuditLogMessage() {
        // given
        String action = "TEST_ACTION";
        String userId = "1L";
        String requestDto = "{\"testKey\": \"testValue\"}";
        String arguments = "param1: value1, param2: value2";

        AuditLogMessageDto messageDto = AuditLogMessageDto.of(
            action,
            userId,
            requestDto,
            arguments
        );

        // when
        auditLogMessageConsumer.processAuditLogMessage(messageDto);

        // then
        AuditLog savedAuditLog = auditLogRepository.findAll().get(0);

        assertThat(savedAuditLog)
            .extracting(
                AuditLog::getAction,
                AuditLog::getUserId,
                AuditLog::getRequestDto,
                AuditLog::getArguments
            )
            .containsExactly(
                action,
                userId,
                requestDto,
                arguments
            );
    }

    @DisplayName("감사 로그 메시지를 여러 건 처리하고 저장할 수 있다.")
    @Test
    void processMultipleAuditLogMessages() {
        // given
        AuditLogMessageDto messageDto1 = AuditLogMessageDto.of(
            "ACTION_1",
            "1L",
            "{\"test1\": \"value1\"}",
            "param1: value1"
        );

        AuditLogMessageDto messageDto2 = AuditLogMessageDto.of(
            "ACTION_2",
            "2L",
            "{\"test2\": \"value2\"}",
            "param2: value2"
        );

        // when
        auditLogMessageConsumer.processAuditLogMessage(messageDto1);
        auditLogMessageConsumer.processAuditLogMessage(messageDto2);

        // then
        assertThat(auditLogRepository.findAll())
            .hasSize(2)
            .extracting(
                AuditLog::getAction,
                AuditLog::getUserId
            )
            .containsExactlyInAnyOrder(
                tuple("ACTION_1", "1L"),
                tuple("ACTION_2", "2L")
            );
    }
}
