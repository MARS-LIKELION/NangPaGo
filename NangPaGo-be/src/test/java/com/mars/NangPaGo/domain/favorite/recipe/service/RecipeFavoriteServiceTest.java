package com.mars.NangPaGo.domain.favorite.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mars.NangPaGo.common.dto.PageDto;
import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.entity.RecipeFavorite;
import com.mars.NangPaGo.domain.favorite.recipe.repository.RecipeFavoriteRepository;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import com.mars.NangPaGo.support.IntegrationTestSupport;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;


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

    @Transactional
    @DisplayName("유저가 레시피 즐겨찾기를 클릭하여 즐겨찾기 추가한다.")
    @Test
    void addRecipeFavorite() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");

        // when
        RecipeFavoriteResponseDto favoriteResponseDto = recipeFavoriteService.toggleFavorite(recipe.getId(),
            user.getEmail());

        // then
        assertThat(favoriteResponseDto)
            .extracting("favorited")
            .isEqualTo(true);
        assertThat(favoriteResponseDto.recipeId()).isEqualTo(recipe.getId());
    }

    @Transactional
    @DisplayName("이미 즐겨찾기를 한 상태에서 유저가 레시피 즐겨찾기를 클릭하여 즐겨찾기 취소한다.")
    @Test
    void cancelRecipeFavorite() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");
        RecipeFavorite recipeFavorite = createRecipeFavorite(user, recipe);

        // when
        RecipeFavoriteResponseDto favoriteResponseDto = recipeFavoriteService.toggleFavorite(recipe.getId(),
            user.getEmail());

        // then
        assertThat(favoriteResponseDto)
            .extracting("favorited")
            .isEqualTo(false);
        assertThat(favoriteResponseDto.recipeId()).isEqualTo(recipe.getId());
    }
    
    @DisplayName("즐겨찾기를 누른 유저의 즐겨찾기 상태는 true 이다.")
    @Test
    void isFavoriteByUser() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");
        RecipeFavorite recipeFavorite = createRecipeFavorite(user, recipe);

        // when
        boolean favorite = recipeFavoriteService.isFavorite(recipe.getId(), user.getEmail());

        // then
        assertThat(favorite).isTrue();
    }

    @DisplayName("다른 유저가 즐겨찾기를 눌렀지만, 또 다른 유저의 즐겨찾기 상태는 false 이다.")
    @Test
    void isNotFavoriteByUser() {
        // given
        User user1 = createUser("dummy@nangpago.com");
        User user2 = createUser("anotherUser@another.com");
        Recipe recipe = createRecipe("파스타");
        RecipeFavorite recipeFavorite = createRecipeFavorite(user2, recipe);


        // when
        boolean favoriteByUser1 = recipeFavoriteService.isFavorite(recipe.getId(), user1.getEmail());
        boolean favoriteByUser2 = recipeFavoriteService.isFavorite(recipe.getId(), user2.getEmail());
        // then
        assertThat(favoriteByUser1).isFalse();
        assertThat(favoriteByUser2).isTrue();
    }

    @DisplayName("유저가 즐겨찾기한 리스트를 조회한다.")
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

        for (Recipe recipe : recipes) {
            createRecipeFavorite(user, recipe);
        }

        Pageable pageable = null;

        // when
        PageDto<RecipeFavoriteListResponseDto> recipeFavorites = recipeFavoriteService.getFavoriteRecipes(
            user.getEmail(), pageable);

        //then
        assertThat(recipeFavorites.getTotalPages()).isEqualTo(1);
        assertThat(recipeFavorites.getTotalItems()).isEqualTo(4);
        assertThat(recipeFavorites.getContent().get(1).name()).isEqualTo("순대국밥");
    }

    @DisplayName("즐겨찾기를 클릭한 유저의 이메일을 찾을 수 없을때 예외를 발생할 수 있다.")
    @Test
    void NotCorrectUserException() {
        // given
        User user = notSaveCreateUser("nonExistent@nangpago.com");
        setAuthenticationAsUserWithToken(user.getEmail());
        Recipe recipe = createRecipe("파스타");

        // when, then
        assertThatThrownBy(() -> recipeFavoriteService.toggleFavorite(recipe.getId(), user.getEmail()))
            .isInstanceOf(NPGException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("유저가 레시피에 즐겨찾기를 클릭할때 레시피 ID를 못받는 경우 예외처리")
    @Test
    void NotFoundRecipeException() {
        // given
        User user = createUser("dummy@nangpago.com");

        // when, then
        assertThatThrownBy(() -> recipeFavoriteService.toggleFavorite(1L, user.getEmail()))
            .isInstanceOf(NPGException.class)
            .hasMessage("레시피를 찾을 수 없습니다.");
    }

    private User notSaveCreateUser(String email){
        return User.builder()
            .email(email)
            .build();
    }
    private User createUser(String email) {
        User user = User.builder()
            .email(email)
            .build();
        return userRepository.save(user);
    }

    private Recipe createRecipe(String name) {
        Recipe recipe = Recipe.builder()
            .name(name)
            .build();
        return recipeRepository.save(recipe);
    }

    private RecipeFavorite createRecipeFavorite(User user, Recipe recipe) {
        return recipeFavoriteRepository.save(RecipeFavorite.of(user, recipe));
    }

    private Recipe createRecipeForPage(String name) {
        Recipe recipe = Recipe.builder()
            .name(name)
            .mainImage("mainUrl")
            .ingredients("초콜릿, 파인애플, 두리안, 참기름")
            .mainIngredient("홍어")
            .calorie(3165)
            .category("반찬")
            .cookingMethod("기타")
            .build();
        return recipeRepository.save(recipe);
    }
}
