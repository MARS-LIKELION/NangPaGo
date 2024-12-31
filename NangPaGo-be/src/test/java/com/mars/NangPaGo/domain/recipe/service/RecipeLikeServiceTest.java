package com.mars.NangPaGo.domain.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.mars.NangPaGo.domain.recipe.dto.RecipeLikeRequestDto;
import com.mars.NangPaGo.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.entity.RecipeLike;
import com.mars.NangPaGo.domain.recipe.repository.RecipeLikeRepository;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import java.util.ArrayList;
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

    @DisplayName("유저 좋아요 클릭")
    @Test
    void toggleRecipeLike() {
        // given
        String email = "dummy@nangpago.com";

        Long recipeId = 1L;
        String recipeName = "파인애플피자";

        User user = User.builder()
            .email(email)
            .build();

        Recipe recipe = new Recipe(
            recipeId,
            recipeName,
            "김치, 두부, 돼지고기, 양파, 대파",
            "1. 재료를 준비한다. 2. 끓인다.",
            "찌개",
            300,
            10,
            20,
            15,
            500,
            "#김치 #찌개",
            "main_image_url.jpg",
            "step_image_url.jpg",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>()
        );

        RecipeLikeRequestDto requestDto =
            new RecipeLikeRequestDto(user.getEmail(), recipe.getId());

        // mocking
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));
        when(recipeLikeRepository.findWithLockByUserAndRecipe(user, recipe)).thenReturn(Optional.empty());

        // when
        RecipeLikeResponseDto recipeLikeResponseDto = recipeLikeService.toggleRecipeLike(requestDto);

        // then
        assertThat(recipeLikeResponseDto).isNotNull()
            .extracting("liked")
            .isEqualTo(true);
    }

    @DisplayName("유저가 좋아요한 상태인지 체크")
    @Test
    void isLikedByUser() {
        // given
        String email = "dummy@nangpago.com";
        long recipeId = 1L;

        User user = User.builder()
            .email(email)
            .build();

        Recipe recipe = new Recipe(
            recipeId,
            "파워에이드라면",
            "김치, 두부, 돼지고기, 양파, 대파",
            "1. 재료를 준비한다. 2. 끓인다.",
            "찌개",
            300,
            10,
            20,
            15,
            500,
            "#김치 #찌개",
            "main_image_url.jpg",
            "step_image_url.jpg",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>()
        );
        RecipeLike recipeLike = RecipeLike.of(user, recipe);
        // mocking
        when(recipeLikeRepository.findByEmailAndRecipeId(email, recipeId)).thenReturn(Optional.ofNullable(recipeLike));

        // when
        boolean liked = recipeLikeService.isLikedByUser(email, recipeId);

        // then
        assertThat(liked).isEqualTo(true);
    }
}
