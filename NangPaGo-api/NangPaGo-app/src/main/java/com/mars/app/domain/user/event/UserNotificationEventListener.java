package com.mars.app.domain.user.event;

import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.app.domain.user.dto.UserNotificationSseDto;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.exception.NPGExceptionType;
import com.mars.common.model.community.Community;
import com.mars.common.model.user.User;
import com.mars.common.sse.AbstractSseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserNotificationEventListener {

    private final AbstractSseEmitterService<UserNotificationSseDto> userNotificationSseService;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @EventListener
    public void handleUserNotificationEvent(UserNotificationEvent event) {
        User sender = userRepository.findById(event.getSenderId())
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);
        Community post = communityRepository.findById(event.getPostId())
            .orElseThrow(NPGExceptionType.NOT_FOUND_COMMUNITY::of);

        String message = sender.getNickname() + "님이 게시물에 댓글을 작성하였습니다.";

        UserNotificationSseDto userNotificationSseDto = UserNotificationSseDto.of(
            event.getSenderId(), event.getPostId(), message
        );
        userNotificationSseService.sendToClient(post.getUser().getId(), userNotificationSseDto);
    }
}
