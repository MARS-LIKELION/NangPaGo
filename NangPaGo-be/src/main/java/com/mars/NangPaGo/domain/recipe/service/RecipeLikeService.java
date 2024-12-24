package com.mars.NangPaGo.domain.recipe.service;

import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.recipe.entity.RecipeLike;
import com.mars.NangPaGo.domain.recipe.repository.RecipeLikeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RecipeLikeService {
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public void toggleRecipeLike(Long recipeId, String email) {
        Optional<RecipeLike> recipeLike = recipeLikeRepository.findByEmailAndRecipeId(email, recipeId);

        toggleRecipeLike(recipeLike, email, recipeId);
    }

    private void toggleRecipeLike(Optional<RecipeLike> recipeLike, String email, Long recipeId) {
        if (recipeLike.isEmpty()) {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
            Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

            recipeLikeRepository.save(RecipeLike.of(user, recipe));
        } else {
            recipeLikeRepository.delete(recipeLike.get());
        }
    }
}
