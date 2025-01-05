package com.mars.NangPaGo.domain.auth.aop;

import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.auth.component.AuthenticationHolder;
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

    @Around("@annotation(AuthenticatedUser)")
    public Object validateAndGetUserEmail(ProceedingJoinPoint joinPoint) throws Throwable {
        String email = AuthenticationHolder.getCurrentUserEmail();
        if (email == null) {
            throw NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of();
        }

        if (Boolean.FALSE.equals(userRepository.existsByEmail(email))) {
            throw NPGExceptionType.NOT_FOUND_USER.of();
        }

        return joinPoint.proceed();
    }
}
