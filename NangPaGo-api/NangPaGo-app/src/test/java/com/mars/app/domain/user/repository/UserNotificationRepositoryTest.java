package com.mars.app.domain.user.repository;

import com.mars.app.support.AbstractRepositoryTestSupport;
import com.mars.common.model.user.UserNotification;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(transactionManager = "mongoTransactionManager")
class UserNotificationRepositoryTest extends AbstractRepositoryTestSupport {

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @DisplayName("특정 유저 알림 데이터 조회 테스트")
    @Test
    void findNotificationsSince() {
        // given
        Long userId = 6L;
        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(14);

        // when
        List<UserNotification> notificationsSince = userNotificationRepository.findNotificationsSince(fourteenDaysAgo,
            userId);

        // then
        for (UserNotification userNotification : notificationsSince) {
            System.out.println("userNotification.getPostId() = " + userNotification.getPostId());
            System.out.println("userNotification.getPostType() = " + userNotification.getPostType());
            System.out.println("userNotification.getTimestamp() = " + userNotification.getTimestamp());
        }
    }
}
