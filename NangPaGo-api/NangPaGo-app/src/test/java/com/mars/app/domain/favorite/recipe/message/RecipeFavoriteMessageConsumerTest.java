package com.mars.app.domain.favorite.recipe.message;

import static org.assertj.core.api.Assertions.*;

import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteMessageDto;
import com.mars.app.domain.recipe.repository.favorite.RecipeFavoriteRepository;
import com.mars.app.domain.recipe.message.favorite.RecipeFavoriteMessageConsumer;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.exception.NPGException;
import com.mars.common.model.favorite.recipe.RecipeFavorite;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RecipeFavoriteMessageConsumerTest extends IntegrationTestSupport {

    @Autowired
    private RecipeFavoriteRepository recipeFavoriteRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeFavoriteMessageConsumer recipeFavoriteMessageConsumer;

    @DisplayName("레시피 즐겨찾기 메시지를 처리하고 즐겨찾기를 추가할 수 있다.")
    @Test
    void processRecipeFavoriteMessageAdd() {
        // given
        User user = createUser("test@nangpago.com");
        Recipe recipe = createRecipe("테스트 레시피");

        userRepository.save(user);
        recipeRepository.save(recipe);

        RecipeFavoriteMessageDto messageDto = RecipeFavoriteMessageDto.of(recipe.getId(), user.getId());

        // when
        recipeFavoriteMessageConsumer.processRecipeFavoriteMessage(messageDto);

        // then
        assertThat(recipeFavoriteRepository.findByUserAndRecipe(user, recipe))
            .isPresent();
    }

    @DisplayName("레시피 즐겨찾기 메시지를 처리하고 즐겨찾기를 취소할 수 있다.")
    @Test
    void processRecipeFavoriteMessageCancel() {
        // given
        User user = createUser("test@nangpago.com");
        Recipe recipe = createRecipe("테스트 레시피");

        userRepository.save(user);
        recipeRepository.save(recipe);

        RecipeFavorite recipeFavorite = RecipeFavorite.of(user, recipe);
        recipeFavoriteRepository.save(recipeFavorite);

        RecipeFavoriteMessageDto messageDto = RecipeFavoriteMessageDto.of(recipe.getId(), user.getId());

        // when
        recipeFavoriteMessageConsumer.processRecipeFavoriteMessage(messageDto);

        // then
        assertThat(recipeFavoriteRepository.findByUserAndRecipe(user, recipe))
            .isEmpty();
    }

    @DisplayName("존재하지 않는 사용자의 즐겨찾기 메시지를 처리할 때 예외가 발생한다.")
    @Test
    void processFavoriteMessageWithInvalidUser() {
        // given
        Recipe recipe = createRecipe("테스트 레시피");
        recipeRepository.save(recipe);

        RecipeFavoriteMessageDto messageDto = RecipeFavoriteMessageDto.of(recipe.getId(), 9999L);

        // when & then
        assertThatThrownBy(() -> recipeFavoriteMessageConsumer.processRecipeFavoriteMessage(messageDto))
            .isInstanceOf(NPGException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("존재하지 않는 레시피의 즐겨찾기 메시지를 처리할 때 예외가 발생한다.")
    @Test
    void processFavoriteMessageWithInvalidRecipe() {
        // given
        User user = createUser("test@nangpago.com");
        userRepository.save(user);

        RecipeFavoriteMessageDto messageDto = RecipeFavoriteMessageDto.of(9999L, user.getId());

        // when & then
        assertThatThrownBy(() -> recipeFavoriteMessageConsumer.processRecipeFavoriteMessage(messageDto))
            .isInstanceOf(NPGException.class)
            .hasMessage("레시피를 찾을 수 없습니다.");
    }

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .build();
    }

    private Recipe createRecipe(String name) {
        return Recipe.builder()
            .name(name)
            .build();
    }
}
