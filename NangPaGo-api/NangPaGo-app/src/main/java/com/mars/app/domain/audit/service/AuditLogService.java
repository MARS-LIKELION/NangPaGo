package com.mars.app.domain.audit.service;

import com.mars.common.model.audit.AuditLog;
import com.mars.app.domain.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    // 감사 로그 생성
    @Transactional
    public void createAuditLog(String action, String userId, String requestDto, String arguments) {
        AuditLog auditLog = AuditLog.builder()
            .action(action)
            .userId(userId)
            .requestDto(requestDto)
            .arguments(arguments)
            .build();

        auditLogRepository.save(auditLog);
    }

    // 모든 감사 로그 조회
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    // 특정 사용자의 감사 로그 조회
    public List<AuditLog> getAuditLogsByUserId(String userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    // 특정 기간 동안의 감사 로그 조회
    public List<AuditLog> getAuditLogsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }

    // 특정 액션에 대한 감사 로그 조회
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }
} 
