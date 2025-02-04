package com.mars.app.domain.audit.model;

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
    private String details;
    private LocalDateTime timestamp;
    
    // You can add more fields based on your actual audit_logs structure

    @Builder
    public AuditLog(String action, String userId, String details) {
        this.action = action;
        this.userId = userId;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
} 