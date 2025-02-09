package com.mars.app.domain.user.service;

import com.mars.app.domain.user.dto.UserNotificationCountResponseDto;
import com.mars.app.domain.user.dto.UserNotificationResponseDto;
import com.mars.app.domain.user.repository.UserNotificationRepository;
import com.mars.common.model.user.UserNotification;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserNotificationService {

    private static final long NOTIFICATION_RETENTION_DAYS = 14;

    private final UserNotificationRepository userNotificationRepository;

    public List<UserNotificationResponseDto> getRecentNotifications(Long userId) {
        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(NOTIFICATION_RETENTION_DAYS);
        List<UserNotification> notificationsSince = userNotificationRepository.findNotificationsSince(fourteenDaysAgo,
            userId);

        return notificationsSince.stream()
            .map(UserNotificationResponseDto::from)
            .toList();
    }

    public UserNotificationCountResponseDto getUnreadNotificationCount(Long userId) {
        long countIsReadFalse = userNotificationRepository.countByUserIdAndIsReadFalse(userId);
        return UserNotificationCountResponseDto.builder()
            .count(countIsReadFalse)
            .build();
    }

    @Transactional
    public UserNotificationCountResponseDto markAsReadToAllNotification(Long userId) {
        long countUpdated = userNotificationRepository.markAllAsReadByUserId(userId);
        return UserNotificationCountResponseDto.builder()
            .count(countUpdated)
            .build();
    }
}
