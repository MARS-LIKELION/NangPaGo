package com.mars.app.domain.user.service;

import static org.assertj.core.api.Assertions.*;

import com.mars.app.domain.user.dto.UserNotificationResponseDto;
import com.mars.app.domain.user.repository.UserNotificationRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.enums.user.UserNotificationEventCode;
import com.mars.common.model.user.UserNotification;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(transactionManager = "mongoTransactionManager")
class UserNotificationServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    private UserNotificationService userNotificationService;

    @DisplayName("사용자의 최근 14일동안의 알림을 조회할 수 있다.")
    @Test
    void getRecentNotifications() {
        // given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyDaysAgo = LocalDateTime.now().minusDays(20);

        UserNotification userNotification1 = createUserNotification(userId, now);
        UserNotification userNotification2 = createUserNotification(userId, now);
        UserNotification userNotification3 = createUserNotification(userId, twentyDaysAgo);
        userNotificationRepository.saveAll(List.of(userNotification1, userNotification2, userNotification3));

        // when
        List<UserNotificationResponseDto> recentNotifications = userNotificationService.getRecentNotifications(userId);

        // then
        assertThat(recentNotifications).hasSize(2);
    }

    private static UserNotification createUserNotification(Long userId, LocalDateTime timestamp) {
        return UserNotification.builder()
            .userId(userId)
            .senderId(999L)
            .userNotificationEventCode(UserNotificationEventCode.USER_RECIPE_LIKE)
            .timestamp(timestamp)
            .build();
    }
}
