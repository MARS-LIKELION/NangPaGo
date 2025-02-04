package com.mars.app.domain.audit.controller;

import com.mars.common.model.audit.AuditLog;
import com.mars.app.domain.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Tag(name = "감사 로그 API", description = "MongoDB 테스트용")
@RequestMapping("/api/audit-logs")
@RestController
public class AuditLogController {
    private final AuditLogService auditLogService;

    @Operation(summary = "전체 감사 로그 조회")
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllAuditLogs());
    }

    @Operation(summary = "사용자별 감사 로그 조회")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByUserId(userId));
    }

    @Operation(summary = "기간별 감사 로그 조회")
    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLog>> getAuditLogsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditLogService.getAuditLogsBetweenDates(start, end));
    }

    @Operation(summary = "액션별 감사 로그 조회")
    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByAction(@PathVariable String action) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByAction(action));
    }
} 
