package com.mars.admin.domain.audit.controller;

import com.mars.admin.domain.audit.dto.AuditLogResponseDto;
import com.mars.admin.domain.audit.service.AuditLogService;
import com.mars.common.dto.ResponseDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/audit")
@RestController
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseDto<PageResponseDto<AuditLogResponseDto>> auditLogs(
        PageRequestVO pageRequestVO
    ) {
        PageResponseDto<AuditLogResponseDto> auditLogs = auditLogService.getAuditLogs(pageRequestVO);
        return ResponseDto.of(auditLogs);
    }
}
