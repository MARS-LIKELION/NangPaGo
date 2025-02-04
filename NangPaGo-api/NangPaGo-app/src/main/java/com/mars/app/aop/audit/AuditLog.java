package com.mars.app.aop.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    String action();  // 수행하는 작업 (예: "COMMENT_CREATE")
    Class<?> dtoType();  // 감사 로그에 포함할 DTO 타입
} 
