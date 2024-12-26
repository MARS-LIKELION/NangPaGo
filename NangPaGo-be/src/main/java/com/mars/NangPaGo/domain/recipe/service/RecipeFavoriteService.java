package com.mars.NangPaGo.domain.recipe.service;

import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.entity.RecipeFavorite;
import com.mars.NangPaGo.domain.recipe.repository.RecipeFavoriteRepository;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeFavoriteService {
    private final RecipeFavoriteRepository recipeFavoriteRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void toggleRecipeFavorite(String email, Long recipeId) {
        Optional<RecipeFavorite> recipeFavorite = recipeFavoriteRepository.findByEmailAndRecipeId(email, recipeId);

        toggleRecipeFavorite(recipeFavorite, email, recipeId);
    }

    private void toggleRecipeFavorite(Optional<RecipeFavorite> recipeFavorite, String email, Long recipeId) {
        if (recipeFavorite.isEmpty()) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("not found user"));
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new EntityNotFoundException("not found recipe"));

            recipeFavoriteRepository.save(RecipeFavorite.of(user, recipe));
        } else {
            recipeFavoriteRepository.delete(recipeFavorite.get());
        }
    }
}
