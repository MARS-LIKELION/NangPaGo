package com.mars.app.domain.recipe.messaging;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.common.exception.NPGExceptionType.SERVER_ERROR;

import com.mars.app.domain.recipe.dto.RecipeLikeMessageDto;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.recipe.RecipeLike;
import com.mars.common.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeNotificationConsumer {

    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    private final SseEmitterService sseEmitterService;

    @Transactional
    @RabbitListener(queues = "#{@likeQueue.name}")
    public void processLikeEvent(RecipeLikeMessageDto recipeLikeMessageDto) {
        toggleLikeStatus(recipeLikeMessageDto.recipeId(), recipeLikeMessageDto.email());

        try {
            publishRecipeLikeEvent(recipeLikeMessageDto);
        } catch (Exception exception) {
            throw SERVER_ERROR.of("SSE 전송 중 에러");
        }
    }

    private int getLikeCount(Long recipeId) {
        return recipeLikeRepository.countByRecipeId(recipeId);
    }

    private void toggleLikeStatus(Long recipeId, String email) {
        User user = getUserByEmail(email);
        Recipe recipe = getRecipeById(recipeId);

        recipeLikeRepository.findByUserAndRecipe(user, recipe)
            .map(this::removeLike)
            .orElseGet(() -> addLike(user, recipe));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자를 찾을 수 없습니다."));
    }

    private Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> NOT_FOUND_RECIPE.of("레시피를 찾을 수 없습니다."));
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
        sseEmitterService.sendLikeCount(recipeLikeMessageDto.recipeId(), likeCount);
    }
}
