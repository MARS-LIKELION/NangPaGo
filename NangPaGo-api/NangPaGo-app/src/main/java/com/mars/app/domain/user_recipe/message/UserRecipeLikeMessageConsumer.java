package com.mars.app.domain.user_recipe.message;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;

import com.mars.app.domain.user.message.UserNotificationMessagePublisher;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.domain.user_recipe.dto.like.UserRecipeLikeMessageDto;
import com.mars.app.domain.user_recipe.event.UserRecipeLikeEvent;
import com.mars.app.domain.user_recipe.repository.UserRecipeLikeRepository;
import com.mars.app.domain.user_recipe.repository.UserRecipeRepository;
import com.mars.common.enums.user.UserNotificationEventCode;
import com.mars.common.model.user.User;
import com.mars.common.model.userRecipe.UserRecipe;
import com.mars.common.model.userRecipe.UserRecipeLike;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserRecipeLikeMessageConsumer {

    private final UserRecipeLikeRepository userRecipeLikeRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher sseEventPublisher;

    private final UserNotificationMessagePublisher userNotificationMessagePublisher;

    @Transactional
    @RabbitListener(queues = "#{@userRecipeLikeQueue.name}")
    public void processLikeMessage(UserRecipeLikeMessageDto messageDto) {
        toggleLikeStatus(messageDto.userRecipeId(), messageDto.userId());
        publishUserRecipeLikeEvent(messageDto);
    }

    private long getLikeCount(Long recipeId) {
        return userRecipeLikeRepository.countByUserRecipeId(recipeId);
    }

    private void toggleLikeStatus(Long recipeId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);
        UserRecipe recipe = userRecipeRepository.findById(recipeId)
            .orElseThrow(() -> NOT_FOUND_RECIPE.of("게시물을 찾을 수 없습니다."));
        userRecipeLikeRepository.findByUserAndUserRecipe(user, recipe)
            .map(this::removeLike)
            .orElseGet(() -> addLike(user, recipe));
    }

    private boolean removeLike(UserRecipeLike userRecipeLike) {
        userRecipeLikeRepository.delete(userRecipeLike);
        return false;
    }

    private boolean addLike(User user, UserRecipe userRecipe) {
        userRecipeLikeRepository.save(UserRecipeLike.of(user, userRecipe));
        userNotificationMessagePublisher.createUserNotification(
            UserNotificationEventCode.USER_RECIPE_LIKE,
            user.getId(),
            userRecipe.getId()
        );
        return true;
    }

    private void publishUserRecipeLikeEvent(UserRecipeLikeMessageDto messageDto) {
        int likeCount = (int) getLikeCount(messageDto.userRecipeId());
        sseEventPublisher.publishEvent(
            UserRecipeLikeEvent.of(
                this,
                messageDto.userRecipeId(),
                messageDto.userId(),
                likeCount
            )
        );
    }
}
