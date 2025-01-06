package com.mars.NangPaGo.common.aop.auth;

import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.common.component.auth.AuthenticationHolder;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class AuthenticationAspect {

    private final UserRepository userRepository;

    @Around("@annotation(com.mars.NangPaGo.common.aop.auth.AuthenticatedUser)")
    public Object validateAndGetUserEmail(ProceedingJoinPoint joinPoint) throws Throwable {
        String email = AuthenticationHolder.getCurrentUserEmail();
        if (email == null || "anonymous_user".equals(email)) {
            throw NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of();
        }

        if (Boolean.FALSE.equals(userRepository.existsByEmail(email))) {
            throw NPGExceptionType.NOT_FOUND_USER.of();
        }

        return joinPoint.proceed();
    }
}
