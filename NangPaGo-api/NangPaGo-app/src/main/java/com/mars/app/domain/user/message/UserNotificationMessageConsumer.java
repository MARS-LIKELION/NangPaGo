package com.mars.app.domain.user.message;

import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.app.domain.user.dto.UserNotificationMessageDto;
import com.mars.app.domain.user.event.UserNotificationEvent;
import com.mars.app.domain.user.repository.UserNotificationRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.domain.user_recipe.repository.UserRecipeRepository;
import com.mars.common.exception.NPGExceptionType;
import com.mars.common.model.user.User;
import com.mars.common.model.user.UserNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserNotificationMessageConsumer {

    private final CommunityRepository communityRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final ApplicationEventPublisher sseEventPublisher;

    @Transactional
    @RabbitListener(queues = "#{@userNotificationQueue.name}")
    public void processUserNotificationMessage(UserNotificationMessageDto messageDto) {
        Long receiverId = findReceiverId(messageDto);
        if (isSenderSameAsReceiver(messageDto, receiverId)) {
            return;
        }

        String senderNickname = findSenderNickname(messageDto);
        saveUserNotification(messageDto, receiverId, senderNickname);
        publishUserNotificationEvent(messageDto, receiverId);
    }

    private Long findReceiverId(UserNotificationMessageDto dto) {
        if (dto.userNotificationEventCode().isCommunityType()) {
            return communityRepository.findById(dto.postId())
                .orElseThrow(NPGExceptionType.NOT_FOUND_COMMUNITY::of)
                .getUser()
                .getId();
        }
        if (dto.userNotificationEventCode().isUserRecipeType()) {
            return userRecipeRepository.findById(dto.postId())
                .orElseThrow(NPGExceptionType.NOT_FOUND_RECIPE::of)
                .getUser()
                .getId();
        }
        throw NPGExceptionType.BAD_REQUEST_INVALID_EVENTCODE.of();
    }

    private boolean isSenderSameAsReceiver(UserNotificationMessageDto messageDto, Long receiverId) {
        return messageDto.senderId().equals(receiverId);
    }

    private String findSenderNickname(UserNotificationMessageDto messageDto) {
        User sender = userRepository.findById(messageDto.senderId())
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);
        return sender.getNickname();
    }

    private void saveUserNotification(UserNotificationMessageDto dto, Long receiverId, String senderNickname) {
        UserNotification entity = dto.toUserNotification(receiverId, senderNickname);
        userNotificationRepository.save(entity);
    }

    private void publishUserNotificationEvent(UserNotificationMessageDto dto, Long receiverId) {
        sseEventPublisher.publishEvent(
            UserNotificationEvent.of(
                this,
                dto.senderId(),
                receiverId,
                dto.postId(),
                dto.userNotificationEventCode()
            )
        );
    }
}
