package com.mars.NangPaGo.domain.favorite.recipe.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;


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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class RecipeFavoriteService {

    private final RecipeFavoriteRepository recipeFavoriteRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeFavoriteResponseDto toggleFavorite(Long recipeId) {
        boolean isFavorite = toggleFavoriteStatus(getAuthenticatedEmail(), recipeId);
        return RecipeFavoriteResponseDto.of(recipeId, isFavorite);
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(Long recipeId) {
        return recipeFavoriteRepository.findByEmailAndRecipeId(getAuthenticatedEmail(), recipeId).isPresent();
    }

    private String getAuthenticatedEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("인증 정보가 존재하지 않습니다.");
        }
        return authentication.getName();
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
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자를 찾을 수 없습니다."));
    }

    private Recipe findRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> NOT_FOUND_RECIPE.of("레시피를 찾을 수 없습니다."));
    }

    private boolean removeFavorite(RecipeFavorite recipeFavorite) {
        recipeFavoriteRepository.delete(recipeFavorite);
        return false;
    }

    private boolean addFavorite(User user, Recipe recipe) {
        recipeFavoriteRepository.save(RecipeFavorite.of(user, recipe));
        return true;
    }

    @Transactional(readOnly = true)
    public List<RecipeFavoriteListResponseDto> getFavoriteRecipes(String email) {
        try {
            User user = findUserByEmail(email);

            List<RecipeFavorite> favorites = recipeFavoriteRepository.findAllByUser(user);

            return favorites.stream()
                .map(favorite -> RecipeFavoriteListResponseDto.of(favorite.getRecipe()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw NPGExceptionType.SERVER_ERROR.of("즐겨찾기 리스트 조회 중 에러 발생: " + e.getMessage());
        }
    }
}
