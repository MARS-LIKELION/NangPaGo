package com.mars.admin.domain.stats.message;

import com.mars.admin.domain.stats.repository.VisitLogRepository;
import com.mars.common.dto.stats.VisitLogMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VisitLogMessageConsumer {

    private final VisitLogRepository visitLogRepository;

    @Transactional
    @RabbitListener(queues = "#{@visitLogQueue.name}")
    public void processVisitLogMessage(VisitLogMessageDto messageDto) {
        visitLogRepository.save(messageDto.toVisitLog());
    }
}
