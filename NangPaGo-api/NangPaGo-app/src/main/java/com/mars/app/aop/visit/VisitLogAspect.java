package com.mars.app.aop.visit;

import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.stats.message.VisitLogMessagePublisher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class VisitLogAspect {

    private final VisitLogMessagePublisher visitLogMessagePublisher;

    @Before("@annotation(com.mars.app.aop.visit.VisitLog)")
    public void logVisit() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = extractIp(request);
        Long userId = AuthenticationHolder.getCurrentUserId();
        
        visitLogMessagePublisher.saveVisitLog(userId, ip, LocalDateTime.now());
    }

    private String extractIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (isInvalidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isInvalidIp(ip)) {
            ip = request.getRemoteAddr();
        }
        
        return formatIp(ip);
    }
    
    private boolean isInvalidIp(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }
    
    private String formatIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "unknown";
        }
        
        // IPv6 로컬호스트 주소 처리
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            return "127.0.0.1";
        }
        
        // X-Forwarded-For 헤더에서 첫 번째 IP 추출
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        // IPv6 주소가 너무 길 경우 축약
        if (ip.length() > 45) {
            return ip.substring(0, 45);
        }
        
        return ip;
    }
}
