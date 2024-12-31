package com.mars.NangPaGo.domain.favorite.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteRequestDto;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.entity.RecipeFavorite;
import com.mars.NangPaGo.domain.favorite.recipe.repository.RecipeFavoriteRepository;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
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
class RecipeFavoriteServiceTest {

    @Mock
    private RecipeFavoriteRepository recipeFavoriteRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecipeFavoriteService recipeFavoriteService;

    @DisplayName("레시피 즐겨찾기")
    @Test
    void toggleFavorite() {
        // given
        String email = "dummy@nangpago.com";
        long recipeId = 1L;

        User user = User.builder()
            .email(email)
            .build();
        Recipe recipe = new Recipe();

        RecipeFavorite recipeFavorite = RecipeFavorite.of(user, recipe);
        RecipeFavoriteRequestDto responseDto = new RecipeFavoriteRequestDto(email, recipeId);

        // mocking
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(recipeFavoriteRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.of(recipeFavorite));

        // when
        RecipeFavoriteResponseDto favoriteResponseDto = recipeFavoriteService.toggleFavorite(responseDto);

        // then
        assertThat(favoriteResponseDto)
            .extracting("isFavorite")
            .isEqualTo(false);
    }

    @DisplayName("해당 레시피를 즐겨찾기 했는지 확인합니다.")
    @Test
    void isFavoriteByUser() {
        // given
        String email = "dummy@nangpago.com";
        long recipeId = 1L;

        User user = User.builder()
            .email(email)
            .build();
        Recipe recipe = new Recipe();

        RecipeFavorite recipeFavorite = RecipeFavorite.of(user, recipe);
        // mocking
        when(recipeFavoriteRepository.findByEmailAndRecipeId(anyString(), anyLong())).thenReturn(
            Optional.ofNullable(recipeFavorite));

        // when
        boolean favorite = recipeFavoriteService.isFavoriteByUser(email, recipeId);
        // then
        assertThat(favorite).isTrue();
    }
}
