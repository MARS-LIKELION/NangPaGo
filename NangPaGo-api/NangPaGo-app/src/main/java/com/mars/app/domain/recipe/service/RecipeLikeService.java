package com.mars.app.domain.recipe.service;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;

import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.recipe.RecipeLike;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeLikeService {

    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public boolean isLiked(Long recipeId, String email) {
        return recipeLikeRepository.findByEmailAndRecipeId(email, recipeId).isPresent();
    }

    public int getLikeCount(Long recipeId) {
        return recipeLikeRepository.countByRecipeId(recipeId);
    }

    @Transactional
    public RecipeLikeResponseDto toggleLike(Long recipeId, String email) {
        boolean isLikedAfterToggle = toggleLikeStatus(recipeId, email);
        return RecipeLikeResponseDto.of(recipeId, isLikedAfterToggle);
    }

    private boolean toggleLikeStatus(Long recipeId, String email) {
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
