package com.mars.app.domain.favorite.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mars.app.domain.recipe.repository.like.RecipeLikeRepository;
import com.mars.app.domain.recipe.service.favorite.RecipeFavoriteService;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteListResponseDto;
import com.mars.common.model.favorite.recipe.RecipeFavorite;
import com.mars.app.domain.recipe.repository.favorite.RecipeFavoriteRepository;
import com.mars.common.model.recipe.Recipe;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.common.model.recipe.RecipeLike;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RecipeFavoriteServiceTest extends IntegrationTestSupport {

    @Autowired
    private RecipeFavoriteRepository recipeFavoriteRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeLikeRepository recipeLikeRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeFavoriteService recipeFavoriteService;

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
        boolean favorite = recipeFavoriteService.isFavorite(recipe.getId(), user.getId());

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
        boolean favoriteByUser1 = recipeFavoriteService.isFavorite(recipe.getId(), user.getId());

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

        PageRequestVO pageRequestVO = PageRequestVO.of(1, 12);

        // when
        PageResponseDto<RecipeFavoriteListResponseDto> recipeFavorites = recipeFavoriteService.getFavoriteRecipes(
            user.getId(), pageRequestVO);

        //then
        assertThat(recipeFavorites)
            .extracting(PageResponseDto::getTotalPages, PageResponseDto::getTotalItems)
            .containsExactly(1, 4L);
        assertThat(recipeFavorites.getContent().get(1).name()).isEqualTo("순대국밥");
    }

    @DisplayName("응답 레시피 리스트에 사용자의 좋아요 상태를 포함시킬 수 있다.")
    @Test
    void getFavoritesRecipesWithLikeStatus() {
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

        PageRequestVO pageRequestVO = PageRequestVO.of(1, 12);

        RecipeLike recipeLike1 = RecipeLike.builder()
            .user(user)
            .recipe(recipes.get(0))
            .build();
        recipeLikeRepository.save(recipeLike1);

        // when
        PageResponseDto<RecipeFavoriteListResponseDto> recipeFavorites = recipeFavoriteService.getFavoriteRecipes(
            user.getId(), pageRequestVO);

        // then
        assertThat(recipeFavorites)
            .extracting(PageResponseDto::getTotalPages, PageResponseDto::getTotalItems)
            .containsExactly(1, 4L);
        assertThat(recipeFavorites.getContent().get(0).isLiked()).isTrue();
        assertThat(recipeFavorites.getContent().get(1).isLiked()).isFalse();
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
