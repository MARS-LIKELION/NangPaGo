package com.mars.common.model.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Document(collection = "audit_logs")
public class AuditLog {
    @Id
    private String id;
    
    private String action;
    private String userId;
    private String requestDto;
    private LocalDateTime timestamp;

    @Builder
    private AuditLog(String action, String userId, String requestDto) {
        this.action = action;
        this.userId = userId;
        this.requestDto = requestDto;
        this.timestamp = LocalDateTime.now();
    }
}
