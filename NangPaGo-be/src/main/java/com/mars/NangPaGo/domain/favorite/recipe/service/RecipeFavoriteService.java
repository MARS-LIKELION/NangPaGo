package com.mars.NangPaGo.domain.favorite.recipe.service;

import com.mars.NangPaGo.common.dto.PageDto;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import com.mars.NangPaGo.domain.favorite.recipe.entity.RecipeFavorite;
import com.mars.NangPaGo.domain.favorite.recipe.repository.RecipeFavoriteRepository;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeFavoriteService {

    private final RecipeFavoriteRepository recipeFavoriteRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional
    public RecipeFavoriteResponseDto toggleFavorite(Long recipeId, String email) {
        boolean isFavorite = toggleFavoriteStatus(email, recipeId);
        return RecipeFavoriteResponseDto.of(recipeId, isFavorite);
    }

    @Transactional
    public boolean isFavorite(Long recipeId, String email) {
        return recipeFavoriteRepository.findByEmailAndRecipeId(email, recipeId).isPresent();
    }

    public PageDto<RecipeFavoriteListResponseDto> getFavoriteRecipes(String email, Pageable pageable) {
        Page<RecipeFavorite> favorites = recipeFavoriteRepository.findAllByUser(findUserByEmail(email), pageable);
        return PageDto.of(
            favorites.map(favorite -> RecipeFavoriteListResponseDto.of(favorite.getRecipe()))
        );
    }

    private boolean toggleFavoriteStatus(String email, Long recipeId) {
        User user = findUserByEmail(email);
        Recipe recipe = findRecipeById(recipeId);

        return recipeFavoriteRepository.findByUserAndRecipe(user, recipe)
            .map(this::removeFavorite)
            .orElseGet(() -> addFavorite(user, recipe));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> NPGExceptionType.NOT_FOUND_USER.of("사용자를 찾을 수 없습니다."));
    }

    private Recipe findRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> NPGExceptionType.NOT_FOUND_RECIPE.of("레시피를 찾을 수 없습니다."));
    }

    private boolean addFavorite(User user, Recipe recipe) {
        recipeFavoriteRepository.save(RecipeFavorite.of(user, recipe));
        return true;
    }

    private boolean removeFavorite(RecipeFavorite recipeFavorite) {
        recipeFavoriteRepository.delete(recipeFavorite);
        return false;
    }
}
