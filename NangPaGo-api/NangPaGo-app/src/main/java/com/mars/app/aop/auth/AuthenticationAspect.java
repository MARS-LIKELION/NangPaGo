package com.mars.app.aop.auth;

import com.mars.common.exception.NPGExceptionType;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.common.model.user.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class AuthenticationAspect {

    @Around("@annotation(com.mars.app.aop.auth.AuthenticatedUser)")
    public Object validateUserById(ProceedingJoinPoint joinPoint) throws Throwable {
        Long userId = AuthenticationHolder.getCurrentUserId();
        if (userId.equals(User.ANONYMOUS_USER_ID)) {
            throw NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of();
        }

        return joinPoint.proceed();
    }
}
