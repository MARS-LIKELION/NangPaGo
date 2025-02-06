package com.mars.admin.domain.audit.service;

import com.mars.admin.domain.audit.dto.AuditLogResponseDto;
import com.mars.admin.domain.audit.repository.AuditLogRepository;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.model.audit.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public PageResponseDto<AuditLogResponseDto> getAuditLogs(PageRequestVO pageRequestVO) {
        Page<AuditLog> auditLogs = auditLogRepository.findAllByOrderByTimestampDesc(pageRequestVO.toPageable());
        return PageResponseDto.of(auditLogs.map(AuditLogResponseDto::from));
    }
}
