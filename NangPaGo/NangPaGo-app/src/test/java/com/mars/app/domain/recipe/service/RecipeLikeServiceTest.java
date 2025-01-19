package com.mars.app.domain.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mars.app.common.exception.NPGException;
import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.app.domain.recipe.entity.Recipe;
import com.mars.app.domain.recipe.entity.RecipeLike;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.app.domain.user.entity.User;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RecipeLikeServiceTest extends IntegrationTestSupport {

    @Autowired
    private RecipeLikeRepository recipeLikeRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeLikeService recipeLikeService;

    @AfterEach
    void tearDown() {
        recipeLikeRepository.deleteAllInBatch();
        recipeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
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

    @Transactional
    @DisplayName("레시피 좋아요를 클릭할 수 있다.")
    @Test
    void addRecipeLike() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");

        userRepository.save(user);
        recipeRepository.save(recipe);

        // when
        RecipeLikeResponseDto recipeLikeResponseDto = recipeLikeService.toggleLike(recipe.getId(), user.getEmail());

        // then
        assertThat(recipeLikeResponseDto).isNotNull()
            .extracting("liked")
            .isEqualTo(true);
        assertThat(recipeLikeResponseDto.recipeId()).isEqualTo(recipe.getId());
    }

    @Transactional
    @DisplayName("레시피 좋아요를 취소할 수 있다.")
    @Test
    void cancelRecipeLike() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");
        RecipeLike recipeLike = RecipeLike.of(user, recipe);

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeLikeRepository.save(recipeLike);

        // when
        RecipeLikeResponseDto recipeLikeResponseDto = recipeLikeService.toggleLike(recipe.getId(), user.getEmail());

        // then
        assertThat(recipeLikeResponseDto).isNotNull()
            .extracting("liked")
            .isEqualTo(false);
    }

    @DisplayName("좋아요를 클릭한 유저의 이메일을 찾을 수 없을 때 예외를 발생시킬 수 있다.")
    @Test
    void notFoundUserException() {
        // given
        User user = createUser("nonExistent@nangpago.com");
        setAuthenticationAsUserWithToken(user.getEmail());
        Recipe recipe = createRecipe("파스타");
        recipeRepository.save(recipe);

        Long recipeId = recipe.getId();
        String email = user.getEmail();

        // when & then
        assertThatThrownBy(() -> recipeLikeService.toggleLike(recipeId, email))
            .isInstanceOf(NPGException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("recipeId 로 레시피를 찾을 수 없을 때 예외를 발생시킬 수 있다.")
    @Test
    void notFoundRecipeException() {
        // given
        User user = createUser("example@nangpago.com");
        userRepository.save(user);

        String email = user.getEmail();

        // when & then
        assertThatThrownBy(() -> recipeLikeService.toggleLike(1L, email))
            .isInstanceOf(NPGException.class)
            .hasMessage("레시피를 찾을 수 없습니다.");
    }

    @DisplayName("좋아요를 누른 레시피는 좋아요 상태가 true 이다.")
    @Test
    void isLikedByUser() {
        // given
        User user = createUser("example@nangpago.com");
        Recipe recipe = createRecipe("파스타");

        userRepository.save(user);
        recipeRepository.save(recipe);

        RecipeLike recipeLike = RecipeLike.builder()
            .user(user)
            .recipe(recipe)
            .build();

        recipeLikeRepository.save(recipeLike);

        // when
        boolean isLiked = recipeLikeService.isLiked(recipe.getId(), user.getEmail());

        // then
        assertThat(isLiked).isTrue();
    }

    @DisplayName("좋아요를 누르지 않은 레시피는 좋아요 상태가 false 이다.")
    @Test
    void isNotLikedByUser() {
        // given
        User user = createUser("example@nangpago.com");
        Recipe recipe = createRecipe("파스타");

        userRepository.save(user);
        recipeRepository.save(recipe);

        // when
        boolean isLiked = recipeLikeService.isLiked(recipe.getId(), user.getEmail());

        // then
        assertThat(isLiked).isFalse();
    }

    @Test
    @DisplayName("다른 사용자가 좋아요한 레시피에 대해 현재 사용자의 좋아요 상태는 false 이다.")
    void isLikedByDifferentUser() {
        // given
        User user1 = createUser("user1@nangpago.com");
        User user2 = createUser("user2@nangpago.com");
        Recipe recipe = createRecipe("파스타");

        userRepository.save(user1);
        userRepository.save(user2);
        recipeRepository.save(recipe);

        RecipeLike recipeLike = RecipeLike.builder()
            .user(user1)
            .recipe(recipe)
            .build();
        recipeLikeRepository.save(recipeLike);

        // when
        boolean isLiked = recipeLikeService.isLiked(recipe.getId(), user2.getEmail());

        // then
        assertThat(isLiked).isFalse();
    }
}
