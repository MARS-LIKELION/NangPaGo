package com.mars.app.domain.user.controller;

import com.mars.app.domain.user.event.UserNotificationSseService;
import com.mars.common.exception.NPGException;
import com.mars.common.util.web.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SseEmitter> streamUserNotification(HttpServletRequest request) {
        try {
            String accessToken = jwtUtil.getAccessTokenFrom(request);
            Long userId = jwtUtil.getId(accessToken);
            SseEmitter emitter = userNotificationSseService.createEmitter(userId);
            return ResponseEntity.ok(emitter);
        } catch (NPGException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}
