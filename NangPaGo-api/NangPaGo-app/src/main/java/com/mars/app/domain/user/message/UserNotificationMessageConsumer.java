package com.mars.app.domain.user.message;

import com.mars.app.domain.user.dto.UserNotificationMessageDto;
import com.mars.app.domain.user.event.UserNotificationEvent;
import com.mars.app.domain.user.repository.UserNotificationRepository;
import com.mars.common.enums.user.UserNotificationEventCode;
import com.mars.common.model.user.UserNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserNotificationMessageConsumer {

    private final UserNotificationRepository userNotificationRepository;
    private final ApplicationEventPublisher sseEventPublisher;

    @Transactional
    @RabbitListener(queues = "#{@userNotificationQueue.name}")
    public void processUserNotificationMessage(UserNotificationMessageDto userNotificationMessageDto) {
        UserNotification entity = userNotificationMessageDto.toEntity();
        userNotificationRepository.save(entity);
        publishUserNotificationEvent(userNotificationMessageDto);
    }

    private void publishUserNotificationEvent(UserNotificationMessageDto userNotificationMessageDto) {
        sseEventPublisher.publishEvent(
            UserNotificationEvent.of(
                this,
                userNotificationMessageDto.senderId(),
                userNotificationMessageDto.postId(),
                UserNotificationEventCode.USER_RECIPE_COMMENT
            )
        );
    }
}
