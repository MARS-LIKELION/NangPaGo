package com.mars.admin.domain.audit.service;

import com.mars.admin.domain.audit.dto.AuditLogResponseDto;
import com.mars.admin.domain.audit.repository.AuditLogRepository;
import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.model.audit.AuditLog;
import com.mars.common.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public PageResponseDto<AuditLogResponseDto> getAuditLogs(PageRequestVO pageRequestVO) {
        Page<AuditLog> auditLogs = auditLogRepository.findAllByOrderByTimestampDesc(pageRequestVO.toPageable());
        return PageResponseDto.of(auditLogs.map(auditLog -> {
            String email = "";
            long userId = Long.parseLong(auditLog.getUserId());
            if (userId != User.ANONYMOUS_USER_ID) {
                email = userRepository.findById(userId)
                    .map(User::getEmail)
                    .orElse("");
            }
            
            return AuditLogResponseDto.of(auditLog, email);
        }));
    }
}
