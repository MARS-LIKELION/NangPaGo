package com.mars.app.domain.user.event;

import com.mars.app.domain.user.dto.UserNotificationSseDto;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.exception.NPGExceptionType;
import com.mars.common.model.user.User;
import com.mars.common.sse.AbstractSseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserNotificationEventListener {

    private final AbstractSseEmitterService<UserNotificationSseDto> userNotificationSseService;

    @EventListener
    public void handleUserNotificationEvent(UserNotificationEvent event) {
        log.warn("event: senderId: {}, receiverId: {}, postId: {}", event.getSenderId(), event.getReceiverId(), event.getPostId());
        UserNotificationSseDto userNotificationSseDto = UserNotificationSseDto.of(
            event.getSenderId(), event.getPostId()
        );
        userNotificationSseService.sendToClient(event.getReceiverId(), userNotificationSseDto);
    }
}
