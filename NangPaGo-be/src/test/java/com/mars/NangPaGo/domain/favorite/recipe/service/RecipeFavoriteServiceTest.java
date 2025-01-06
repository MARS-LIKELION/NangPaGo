package com.mars.NangPaGo.domain.favorite.recipe.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mars.NangPaGo.common.exception.NPGException;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private long recipeId;
    private String email;
    private Recipe recipe;
    private User user;

    public void setUp() {
        // given
        recipeId = 1L;
        email = "dummy@nangpago.com";

        recipe = Recipe.builder()
            .id(recipeId)
            .build();

        user = User.builder()
            .email(email)
            .build();

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @DisplayName("유저가 레시피 즐겨찾기를 클릭하여 즐겨찾기 추가")
    @Test
    void RecipeFavoriteAdd() {
        // given
        setUp();

        // mocking
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(recipeFavoriteRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.empty());

        // when
        RecipeFavoriteResponseDto favoriteResponseDto = recipeFavoriteService.toggleFavorite(recipeId);

        // then
        verify(recipeFavoriteRepository, times(1)).findByUserAndRecipe(user, recipe);

        assertThat(favoriteResponseDto)
            .extracting("favorited")
            .isEqualTo(true);
    }

    @DisplayName("이미 즐겨찾기를 한 상태에서 유저가 레시피 즐겨찾기를 클릭하여 즐겨찾기 취소")
    @Test
    void RecipeFavoriteCancel() {
        // given
        setUp();
        RecipeFavorite recipeFavorite = RecipeFavorite.of(user, recipe);

        // mocking
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(recipeFavoriteRepository.findByUserAndRecipe(user, recipe)).thenReturn(Optional.of(recipeFavorite));

        // when
        RecipeFavoriteResponseDto favoriteResponseDto = recipeFavoriteService.toggleFavorite(recipeId);

        // then
        verify(recipeFavoriteRepository, times(1)).findByUserAndRecipe(user, recipe);

        assertThat(favoriteResponseDto)
            .extracting("favorited")
            .isEqualTo(false);
    }

    @DisplayName("유저가 해당 레시피를 즐겨찾기 했는지 확인합니다.")
    @Test
    void isFavoriteByUser() {
        // given
        setUp();
        RecipeFavorite recipeFavorite = RecipeFavorite.of(user, recipe);

        // mocking
        when(recipeFavoriteRepository.findByEmailAndRecipeId(anyString(), anyLong())).thenReturn(
            Optional.ofNullable(recipeFavorite));

        // when
        boolean favorite = recipeFavoriteService.isFavorite(recipeId);
        
        // then
        verify(recipeFavoriteRepository, times(1)).findByEmailAndRecipeId(email, recipeId);

        assertThat(favorite).isTrue();
    }

    @DisplayName("유저가 레시피에 즐겨찾기를 클릭할때 SecurityContext의 email과 DB의 유저 email이 일치하지 않을 경우 예외처리")
    @Test
    void NotCorrectUserException() {
        // given
        setUp();

        // mocking
        when(userRepository.findByEmail(anyString())).thenThrow(new NPGException(NOT_FOUND_USER));

        // when, then
        assertThatThrownBy(() -> recipeFavoriteService.toggleFavorite(recipeId))
            .isInstanceOf(NPGException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("유저가 레시피에 즐겨찾기를 클릭할때 레시피 ID를 못받는 경우 예외처리")
    @Test
    void NotFoundRecipeException() {
        // given
        setUp();

        // mocking
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenThrow(new NPGException(NOT_FOUND_RECIPE));

        // when, then
        assertThatThrownBy(() -> recipeFavoriteService.toggleFavorite(recipeId))
            .isInstanceOf(NPGException.class)
            .hasMessage("레시피를 찾을 수 없습니다.");
    }
}
