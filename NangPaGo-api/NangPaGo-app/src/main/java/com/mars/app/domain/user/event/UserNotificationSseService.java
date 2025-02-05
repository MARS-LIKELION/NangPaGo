package com.mars.app.domain.user.event;

import com.mars.app.domain.user.dto.UserNotificationSseDto;
import com.mars.common.sse.AbstractSseEmitterService;
import org.springframework.stereotype.Service;

@Service
public class UserNotificationSseService extends AbstractSseEmitterService<UserNotificationSseDto> {

    private static final String USER_NOTIFICATION_EVENT_NAME = "USER_NOTIFICATION_EVENT";

    @Override
    protected String getEventName() {
        return USER_NOTIFICATION_EVENT_NAME;
    }
}
