package com.mars.app.domain.recipe.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;

import com.mars.app.domain.recipe.dto.RecipeLikeMessageDto;
import com.mars.app.domain.recipe.event.RecipeLikeEvent;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.exception.NPGException;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.recipe.RecipeLike;
import com.mars.common.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

class RecipeLikeMessageConsumerTest extends IntegrationTestSupport {

    @Autowired
    private RecipeLikeRepository recipeLikeRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher sseEventPublisher;

    @Autowired
    private RecipeLikeMessageConsumer recipeLikeMessageConsumer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recipeLikeMessageConsumer, "sseEventPublisher", sseEventPublisher);
    }

    @AfterEach
    void tearDown() {
        recipeLikeRepository.deleteAllInBatch();
        recipeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("레시피 좋아요 메시지를 처리하고 좋아요를 추가할 수 있다.")
    @Test
    void processLikeMessageAdd() {
        // given
        User user = createUser("test@nangpago.com");
        Recipe recipe = createRecipe("테스트 레시피");

        userRepository.save(user);
        recipeRepository.save(recipe);

        RecipeLikeMessageDto messageDto = RecipeLikeMessageDto.of(recipe.getId(), user.getId());

        // when
        recipeLikeMessageConsumer.processLikeMessage(messageDto);

        // then
        verify(sseEventPublisher).publishEvent(
            argThat(event -> {
                RecipeLikeEvent recipeLikeEvent = (RecipeLikeEvent) event;
                return recipeLikeEvent.getRecipeId().equals(recipe.getId()) &&
                       recipeLikeEvent.getUserId().equals(user.getId()) &&
                       recipeLikeEvent.getLikeCount() == 1;
            })
        );
        assertThat(recipeLikeRepository.findByUserIdAndRecipeId(user.getId(), recipe.getId()))
            .isPresent();
        assertThat(recipeLikeRepository.countByRecipeId(recipe.getId())).isEqualTo(1);
    }

    @DisplayName("레시피 좋아요 메시지를 처리하고 좋아요를 취소할 수 있다.")
    @Test
    void processLikeMessageRemove() {
        // given
        User user = createUser("test@nangpago.com");
        Recipe recipe = createRecipe("테스트 레시피");

        userRepository.save(user);
        recipeRepository.save(recipe);

        RecipeLike recipeLike = RecipeLike.of(user, recipe);
        recipeLikeRepository.save(recipeLike);

        RecipeLikeMessageDto messageDto = RecipeLikeMessageDto.of(recipe.getId(), user.getId());

        // when
        recipeLikeMessageConsumer.processLikeMessage(messageDto);

        // then
        verify(sseEventPublisher).publishEvent(
            argThat(event -> {
                RecipeLikeEvent recipeLikeEvent = (RecipeLikeEvent) event;
                return recipeLikeEvent.getRecipeId().equals(recipe.getId()) &&
                       recipeLikeEvent.getUserId().equals(user.getId()) &&
                       recipeLikeEvent.getLikeCount() == 0;
            })
        );
        assertThat(recipeLikeRepository.findByUserIdAndRecipeId(user.getId(), recipe.getId()))
            .isEmpty();
        assertThat(recipeLikeRepository.countByRecipeId(recipe.getId())).isZero();
    }

    @DisplayName("존재하지 않는 사용자의 좋아요 메시지를 처리할 때 예외가 발생한다.")
    @Test
    void processLikeMessageWithInvalidUser() {
        // given
        Recipe recipe = createRecipe("테스트 레시피");
        recipeRepository.save(recipe);

        RecipeLikeMessageDto messageDto = RecipeLikeMessageDto.of(recipe.getId(), 9999L);

        // when & then
        assertThatThrownBy(() -> recipeLikeMessageConsumer.processLikeMessage(messageDto))
            .isInstanceOf(NPGException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("존재하지 않는 레시피의 좋아요 메시지를 처리할 때 예외가 발생한다.")
    @Test
    void processLikeMessageWithInvalidRecipe() {
        // given
        User user = createUser("test@nangpago.com");
        userRepository.save(user);

        RecipeLikeMessageDto messageDto = RecipeLikeMessageDto.of(999L, user.getId());

        // when & then
        assertThatThrownBy(() -> recipeLikeMessageConsumer.processLikeMessage(messageDto))
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
