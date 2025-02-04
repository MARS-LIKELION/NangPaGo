package com.mars.app.aop.audit;

import com.mars.common.enums.audit.AuditActionType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    AuditActionType action();
    Class<?> dtoType() default Void.class;
} 
