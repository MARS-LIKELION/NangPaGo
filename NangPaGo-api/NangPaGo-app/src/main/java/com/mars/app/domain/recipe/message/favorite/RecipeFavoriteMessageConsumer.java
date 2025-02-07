package com.mars.app.domain.recipe.message.favorite;

import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteMessageDto;
import com.mars.app.domain.recipe.repository.favorite.RecipeFavoriteRepository;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.exception.NPGExceptionType;
import com.mars.common.model.favorite.recipe.RecipeFavorite;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecipeFavoriteMessageConsumer {

    private final RecipeFavoriteRepository recipeFavoriteRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional
    @RabbitListener(queues = "#{recipeFavoriteQueue.name}")
    public void processRecipeFavoriteMessage(RecipeFavoriteMessageDto recipeFavoriteMessageDto) {
        toggleFavoriteStatus(recipeFavoriteMessageDto.userId(), recipeFavoriteMessageDto.recipeId());
    }

    private void toggleFavoriteStatus(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(NPGExceptionType.NOT_FOUND_RECIPE::of);

        recipeFavoriteRepository.findByUserAndRecipe(user, recipe)
            .map(this::removeFavorite)
            .orElseGet(() -> addFavorite(user, recipe));
    }

    private boolean addFavorite(User user, Recipe recipe) {
        recipeFavoriteRepository.save(RecipeFavorite.of(user, recipe));
        return true;
    }

    private boolean removeFavorite(RecipeFavorite recipeFavorite) {
        recipeFavoriteRepository.delete(recipeFavorite);
        return false;
    }
}
