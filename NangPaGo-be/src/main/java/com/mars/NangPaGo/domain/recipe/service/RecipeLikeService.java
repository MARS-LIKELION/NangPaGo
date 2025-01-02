package com.mars.NangPaGo.domain.recipe.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;

import com.mars.NangPaGo.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.entity.RecipeLike;
import com.mars.NangPaGo.domain.recipe.repository.RecipeLikeRepository;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class RecipeLikeService {

    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean isLikedByRecipe(Long recipeId) {
        return recipeLikeRepository.findByEmailAndRecipeId(getAuthenticatedEmail(), recipeId).isPresent();
    }

    public RecipeLikeResponseDto toggleLike(Long recipeId) {
        boolean isLiked = toggleLike(getAuthenticatedEmail(), recipeId);
        return RecipeLikeResponseDto.of(recipeId, isLiked);
    }

    private String getAuthenticatedEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("인증 정보가 존재하지 않습니다.");
        }
        return authentication.getName();
    }

    private boolean toggleLike(String email, Long recipeId) {
        User user = getUserByEmail(email);
        Recipe recipe = getRecipeById(recipeId);

        return recipeLikeRepository.findByUserAndRecipe(user, recipe)
            .map(this::removeLike)
            .orElseGet(() -> addLike(user, recipe));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자를 찾을 수 없습니다."));
    }
    private Recipe getRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> NOT_FOUND_RECIPE.of("레시피를 찾을 수 없습니다."));
    }

    private boolean removeLike(RecipeLike recipeLike) {
        recipeLikeRepository.delete(recipeLike);
        return false;
    }

    private boolean addLike(User user, Recipe recipe) {
        recipeLikeRepository.save(RecipeLike.of(user, recipe));
        return true;
    }
}
