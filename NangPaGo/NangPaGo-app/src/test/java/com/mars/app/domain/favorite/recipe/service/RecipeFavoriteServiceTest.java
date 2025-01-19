package com.mars.app.domain.favorite.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mars.app.common.dto.PageDto;
import com.mars.app.common.exception.NPGException;
import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import com.mars.app.domain.favorite.recipe.entity.RecipeFavorite;
import com.mars.app.domain.favorite.recipe.repository.RecipeFavoriteRepository;
import com.mars.app.domain.recipe.entity.Recipe;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.app.domain.user.entity.User;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RecipeFavoriteServiceTest extends IntegrationTestSupport {

    @Autowired
    private RecipeFavoriteRepository recipeFavoriteRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeFavoriteService recipeFavoriteService;

    @AfterEach
    void tearDown() {
        recipeFavoriteRepository.deleteAllInBatch();
        recipeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("레시피를 즐겨찾기 할 수 있다.")
    @Test
    void addRecipeFavorite() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");

        userRepository.save(user);
        recipeRepository.save(recipe);

        // when
        RecipeFavoriteResponseDto favoriteResponseDto = recipeFavoriteService.toggleFavorite(recipe.getId(),
            user.getEmail());

        // then
        assertThat(favoriteResponseDto)
            .extracting("favorited", "recipeId")
            .containsExactly(true, recipe.getId());
    }

    @DisplayName("등록된 레시피 즐겨찾기를 취소한다.")
    @Test
    void cancelRecipeFavorite() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");
        RecipeFavorite recipeFavorite = createRecipeFavorite(user, recipe);

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeFavoriteRepository.save(recipeFavorite);

        // when
        RecipeFavoriteResponseDto favoriteResponseDto = recipeFavoriteService.toggleFavorite(recipe.getId(),
            user.getEmail());

        // then
        assertThat(favoriteResponseDto)
            .extracting("favorited", "recipeId")
            .containsExactly(false, recipe.getId());
    }

    @DisplayName("즐겨찾기한 레시피의 즐겨찾기 상태는 true 이다.")
    @Test
    void isFavoriteByUser() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");
        RecipeFavorite recipeFavorite = createRecipeFavorite(user, recipe);

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeFavoriteRepository.save(recipeFavorite);

        // when
        boolean favorite = recipeFavoriteService.isFavorite(recipe.getId(), user.getEmail());

        // then
        assertThat(favorite).isTrue();
    }

    @DisplayName("즐겨찾기하지 않은 레시피의 즐겨찾기 상태는 false 이다.")
    @Test
    void isNotFavoriteByUser() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");
        recipeRepository.save(recipe);
        
        // when
        boolean favoriteByUser1 = recipeFavoriteService.isFavorite(recipe.getId(), user.getEmail());

        // then
        assertThat(favoriteByUser1).isFalse();
    }

    @DisplayName("즐겨찾기한 리스트를 조회한다.")
    @Test
    void findMyFavoritePage() {
        // given
        User user = createUser("dummy@nangpago.com");

        List<Recipe> recipes = Arrays.asList(
            createRecipeForPage("쭈꾸미덮밥"),
            createRecipeForPage("순대국밥"),
            createRecipeForPage("돈까스"),
            createRecipeForPage("파스타")
        );

        List<RecipeFavorite> favorites = recipes.stream().map(
            recipe -> createRecipeFavorite(user, recipe)).toList();

        userRepository.save(user);
        recipeRepository.saveAll(recipes);
        recipeFavoriteRepository.saveAll(favorites);

        // when
        PageDto<RecipeFavoriteListResponseDto> recipeFavorites = recipeFavoriteService.getFavoriteRecipes(
            user.getEmail(), null);

        //then
        assertThat(recipeFavorites)
            .extracting(PageDto::getTotalPages, PageDto::getTotalItems)
            .containsExactly(1, 4L);
        assertThat(recipeFavorites.getContent().get(1).name()).isEqualTo("순대국밥");
    }

    @DisplayName("사용자를 찾을 수 없을 때 예외를 발생시킬 수 있다.")
    @Test
    void NotCorrectUserException() {
        // given
        Recipe recipe = createRecipe("파스타");

        recipeRepository.save(recipe);

        String email = "dummy@nangpago.com";

        // when, then
        assertThatThrownBy(() -> recipeFavoriteService.toggleFavorite(recipe.getId(), email))
            .isInstanceOf(NPGException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("레시피를 찾을 수 없을 때 예외를 발생시킬 수 있다.")
    @Test
    void NotFoundRecipeException() {
        // given
        User user = createUser("dummy@nangpago.com");

        userRepository.save(user);

        Long recipeId = 1L;

        // when, then
        assertThatThrownBy(() -> recipeFavoriteService.toggleFavorite(recipeId, user.getEmail()))
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

    private RecipeFavorite createRecipeFavorite(User user, Recipe recipe) {
        return RecipeFavorite.of(user, recipe);
    }

    private Recipe createRecipeForPage(String name) {
        return Recipe.builder()
            .name(name)
            .mainImage("mainUrl")
            .ingredients("초콜릿, 파인애플, 두리안, 참기름")
            .mainIngredient("홍어")
            .calorie(3165)
            .category("반찬")
            .cookingMethod("기타")
            .build();
    }
}
