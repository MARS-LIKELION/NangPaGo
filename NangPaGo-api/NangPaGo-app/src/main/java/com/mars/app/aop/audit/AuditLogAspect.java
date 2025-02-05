package com.mars.app.aop.audit;

import com.mars.app.domain.audit.message.AuditLogMessagePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import com.mars.app.component.auth.AuthenticationHolder;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class AuditLogAspect {

    private final AuditLogMessagePublisher auditLogMessagePublisher;
    private final ObjectMapper objectMapper;

    @AfterReturning(pointcut = "@annotation(com.mars.app.aop.audit.AuditLog)")
    public void logAudit(JoinPoint joinPoint) {
        // 현재 실행된 메서드의 AuditLog 어노테이션 정보 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuditLog auditLogAnnotation = method.getAnnotation(AuditLog.class);

        String userId = String.valueOf(AuthenticationHolder.getCurrentUserId());
        String action = auditLogAnnotation.action().toString();

        // DTO 찾기 및 JSON 변환
        Object[] args = joinPoint.getArgs();
        String requestDto = getRequestDto(args, auditLogAnnotation);
        String arguments = getOtherArgs(args, auditLogAnnotation, signature);

        auditLogMessagePublisher.createAuditLog(action, userId, requestDto, arguments);
    }

    private String getRequestDto(Object[] args, AuditLog auditLogAnnotation) {
        return Arrays.stream(args)
            .filter(arg -> auditLogAnnotation.dtoType().isInstance(arg))
            .findFirst()
            .map(arg -> {
                try {
                    return objectMapper.writeValueAsString(arg);
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize DTO to JSON", e);
                    return "{}";
                }
            })
            .orElse("{}");
    }
    
    private String getOtherArgs(Object[] args, AuditLog auditLogAnnotation, MethodSignature signature) {
        String[] parameterNames = signature.getParameterNames();
        
        return IntStream.range(0, args.length)
            .filter(i -> args[i] != null && !auditLogAnnotation.dtoType().isInstance(args[i]))
            .mapToObj(i -> parameterNames[i] + ": " + args[i].toString())
            .collect(Collectors.joining(", "));
    }
}
