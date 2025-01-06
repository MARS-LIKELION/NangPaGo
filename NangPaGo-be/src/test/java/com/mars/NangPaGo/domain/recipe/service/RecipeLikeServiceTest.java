package com.mars.NangPaGo.domain.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mars.NangPaGo.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.entity.RecipeLike;
import com.mars.NangPaGo.domain.recipe.repository.RecipeLikeRepository;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;

import com.mars.NangPaGo.support.IntegrationTestSupport;
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
    @DisplayName("유저가 레시피에 좋아요를 클릭하여 RecipeLike 추가")
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

    @DisplayName("이미 좋아요를 누른 레시피에서 유저가 레시피에 좋아요를 클릭하여 RecipeLike 삭제")
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

//    @DisplayName("유저가 레시피에 좋아요를 클릭할때 SecurityContext의 email과 DB의 유저 email이 일치하지 않을 경우 예외처리")
//    @Test
//    void NotCorrectUserException() {
//        // given
//        setUp();
//
//        // mocking
//        when(userRepository.findByEmail(anyString())).thenThrow(new NPGException(NOT_FOUND_USER));
//
//        // then
//        assertThatThrownBy(() -> recipeLikeService.toggleLike(recipeId, email))
//            .isInstanceOf(NPGException.class)
//            .hasMessage("사용자를 찾을 수 없습니다.");
//    }
//
//    @DisplayName("유저가 레시피에 좋아요를 클릭할때 레시피 ID를 못받는 경우 예외처리")
//    @Test
//    void NotFoundRecipeException() {
//        // given
//        setUp();
//        RecipeLike recipeLike = RecipeLike.of(user, recipe);
//
//        // mocking
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//        when(recipeRepository.findById(anyLong())).thenThrow(new NPGException(NOT_FOUND_RECIPE));
//
//        // when, then
//        assertThatThrownBy(() -> recipeLikeService.toggleLike(recipeId, email))
//            .isInstanceOf(NPGException.class)
//            .hasMessage("레시피를 찾을 수 없습니다.");
//    }
//
//    @DisplayName("이미 좋아요를 누른 레시피는 RecipeLike 엔티티를 조회할 수 있다.")
//    @Test
//    void isLikedByUser() {
//        // given
//        setUp();
//        RecipeLike recipeLike = RecipeLike.of(user, recipe);
//
//        // mocking
//        when(recipeLikeRepository.findByEmailAndRecipeId(anyString(), anyLong())).thenReturn(
//            Optional.ofNullable(recipeLike));
//
//        // when
//        boolean liked = recipeLikeService.isLikedByRecipe(recipeId, email);
//
//        // then
//        assertThat(liked).isTrue();
//    }
}
