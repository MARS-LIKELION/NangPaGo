package com.mars.app.domain.recipe.message.like;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;

import com.mars.app.domain.recipe.dto.like.RecipeLikeMessageDto;
import com.mars.app.domain.recipe.event.RecipeLikeEvent;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.recipe.RecipeLike;
import com.mars.common.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecipeLikeMessageConsumer {

    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher sseEventPublisher;

    @Transactional
    @RabbitListener(queues = "#{@recipeLikeQueue.name}")
    public void processLikeMessage(RecipeLikeMessageDto recipeLikeMessageDto) {
        toggleLikeStatus(recipeLikeMessageDto.recipeId(), recipeLikeMessageDto.userId());
        publishRecipeLikeEvent(recipeLikeMessageDto);
    }

    private int getLikeCount(Long recipeId) {
        return recipeLikeRepository.countByRecipeId(recipeId);
    }

    private void toggleLikeStatus(Long recipeId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(NOT_FOUND_RECIPE::of);

        recipeLikeRepository.findByUserAndRecipe(user, recipe)
            .map(this::removeLike)
            .orElseGet(() -> addLike(user, recipe));
    }

    private boolean removeLike(RecipeLike recipeLike) {
        recipeLikeRepository.delete(recipeLike);
        return false;
    }

    private boolean addLike(User user, Recipe recipe) {
        recipeLikeRepository.save(RecipeLike.of(user, recipe));
        return true;
    }

    private void publishRecipeLikeEvent(RecipeLikeMessageDto recipeLikeMessageDto) {
        int likeCount = getLikeCount(recipeLikeMessageDto.recipeId());
        sseEventPublisher.publishEvent(
            RecipeLikeEvent.of(
                this,
                recipeLikeMessageDto.recipeId(),
                recipeLikeMessageDto.userId(),
                likeCount
            )
        );
    }
}
