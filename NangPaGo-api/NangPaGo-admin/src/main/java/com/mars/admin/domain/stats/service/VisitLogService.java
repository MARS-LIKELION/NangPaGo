package com.mars.admin.domain.stats.service;

import com.mars.admin.domain.stats.repository.VisitLogRepository;
import com.mars.common.model.stats.VisitLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VisitLogService {

    private final VisitLogRepository visitLogRepository;

    public void saveVisitLog(Long userId, String ip) {
        visitLogRepository.save(VisitLog.of(userId, ip));
    }
}
