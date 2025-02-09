package com.mars.app.domain.user.controller;

import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.user.dto.UserNotificationCountResponseDto;
import com.mars.app.domain.user.dto.UserNotificationResponseDto;
import com.mars.app.domain.user.event.UserNotificationSseService;
import com.mars.app.domain.user.service.UserNotificationService;
import com.mars.common.dto.ResponseDto;
import com.mars.common.exception.NPGException;
import com.mars.common.model.user.UserNotification;
import com.mars.common.util.web.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "유저 알림 API", description = "유저 알림 SSE 구독, 알림 조회")
@RequiredArgsConstructor
@RequestMapping("/api/user/notification")
@RestController
public class UserNotificationController {

    private final UserNotificationService userNotificationService;
    private final UserNotificationSseService userNotificationSseService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "유저 알림 SSE 구독")
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

    @Operation(summary = "유저 알림 리스트 조회")
    @AuthenticatedUser
    @GetMapping("/list")
    public ResponseDto<List<UserNotificationResponseDto>> getNotificationList() {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userNotificationService.getRecentNotifications(userId));
    }

    @Operation(summary = "미확인 알림 개수 조회")
    @AuthenticatedUser
    @GetMapping("/count")
    public ResponseDto<UserNotificationCountResponseDto> getUnreadNotificationCount() {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(userNotificationService.getUnreadNotificationCount(userId));
    }
}
