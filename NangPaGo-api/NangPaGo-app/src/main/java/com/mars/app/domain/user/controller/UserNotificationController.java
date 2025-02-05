package com.mars.app.domain.user.controller;

import com.mars.app.domain.user.event.UserNotificationSseService;
import com.mars.common.util.web.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "유저 알림 API", description = "유저 알림 구독")
@RequiredArgsConstructor
@RequestMapping("/api/user/notification")
@RestController
public class UserNotificationController {

    private final UserNotificationSseService userNotificationSseService;
    private final JwtUtil jwtUtil;

    @GetMapping("/subscribe")
    public SseEmitter streamUserNotification(HttpServletRequest request) {
        String accessToken = jwtUtil.getAccessTokenFrom(request);
        Long userId = jwtUtil.getId(accessToken);
        return userNotificationSseService.createEmitter(userId);
    }
}
