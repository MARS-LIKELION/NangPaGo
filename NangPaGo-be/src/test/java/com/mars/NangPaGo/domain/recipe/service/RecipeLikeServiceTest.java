package com.mars.NangPaGo.domain.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.mars.NangPaGo.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.entity.RecipeLike;
import com.mars.NangPaGo.domain.recipe.repository.RecipeLikeRepository;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class RecipeLikeServiceTest {

    @Mock
    private RecipeLikeRepository recipeLikeRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecipeLikeService recipeLikeService;

    @DisplayName("유저가 레시피에 좋아요를 클릭하여 RecipeLike 추가")
    @Test
    void addRecipeLike() {
        // given
        long recipeId = 1L;
        String email = "dummy@nangpago.com";
        Recipe recipe = Recipe.builder()
            .id(recipeId)
            .build();
        User user = User.builder()
            .email(email)
            .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(recipeLikeRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.empty());

        // when
        RecipeLikeResponseDto recipeLikeResponseDto = recipeLikeService.toggleLike(recipeId, email);

        // then
        assertThat(recipeLikeResponseDto).isNotNull()
            .extracting("liked")
            .isEqualTo(true);
    }

    @DisplayName("이미 좋아요를 누른 레시피에서 유저가 레시피에 좋아요를 클릭하여 RecipeLike 삭제")
    @Test
    void cancelRecipeLike() {
        // given
        long recipeId = 1L;
        String email = "dummy@nangpago.com";
        Recipe recipe = Recipe.builder()
            .id(recipeId)
            .build();
        User user = User.builder()
            .email(email)
            .build();
        RecipeLike recipeLike = RecipeLike.of(user, recipe);

        // mocking
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(recipeLikeRepository.findByUserAndRecipe(any(User.class), any(Recipe.class))).thenReturn(
            Optional.of(recipeLike));

        // when
        RecipeLikeResponseDto recipeLikeResponseDto = recipeLikeService.toggleLike(recipeId, email);

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
