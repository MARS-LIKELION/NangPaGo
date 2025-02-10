package com.mars.app.domain.user.service;

import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.app.domain.user.dto.UserNotificationCountResponseDto;
import com.mars.app.domain.user.dto.UserNotificationMessageDto;
import com.mars.app.domain.user.dto.UserNotificationResponseDto;
import com.mars.app.domain.user.repository.UserNotificationRepository;
import com.mars.app.domain.user_recipe.repository.UserRecipeRepository;
import com.mars.common.enums.user.UserNotificationEventCode;
import com.mars.common.exception.NPGExceptionType;
import com.mars.common.model.user.User;
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
    private final CommunityRepository communityRepository;
    private final UserRecipeRepository userRecipeRepository;

    @Transactional
    public List<UserNotificationResponseDto> getRecentNotifications(Long userId) {
        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(NOTIFICATION_RETENTION_DAYS);
        List<UserNotification> notificationsSince = userNotificationRepository.findNotificationsSince(fourteenDaysAgo,
            userId);

        // 리스트 조회와 동시에 읽음 처리
        userNotificationRepository.markAllAsReadByUserId(userId);

        return notificationsSince.stream()
            .map((userNotification) -> {
                String senderNickname = findAuthorNicknameBy(userNotification);
                return UserNotificationResponseDto.of(userNotification, senderNickname);
            })
            .toList();
    }

    public UserNotificationCountResponseDto getUnreadNotificationCount(Long userId) {
        long countIsReadFalse = userNotificationRepository.countByUserIdAndIsReadFalse(userId);
        return UserNotificationCountResponseDto.of(countIsReadFalse);
    }

    @Transactional
    public UserNotificationCountResponseDto deleteUserNotificationBy(Long userId) {
        long deletedCount = userNotificationRepository.deleteByUserId(userId);
        return UserNotificationCountResponseDto.of(deletedCount);
    }

    private String findAuthorNicknameBy(UserNotification userNotification) {
        UserNotificationEventCode eventCode = UserNotificationEventCode.from(
            userNotification.getUserNotificationEventCode());

        if (eventCode.isCommunityType()) {
            return communityRepository.findById(userNotification.getPostId())
                .orElseThrow(NPGExceptionType.NOT_FOUND_COMMUNITY::of)
                .getUser()
                .getNickname();
        }
        if (eventCode.isUserRecipeType()) {
            return userRecipeRepository.findById(userNotification.getPostId())
                .orElseThrow(NPGExceptionType.NOT_FOUND_RECIPE::of)
                .getUser()
                .getNickname();
        }
        throw NPGExceptionType.BAD_REQUEST_INVALID_EVENTCODE.of();
    }
}
