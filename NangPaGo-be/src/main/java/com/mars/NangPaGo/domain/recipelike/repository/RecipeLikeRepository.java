package com.mars.NangPaGo.domain.recipelike.repository;

import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipelike.entity.RecipeLike;
import com.mars.NangPaGo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {
    Optional<RecipeLike> findByUserAndRecipe(User user, Recipe recipe);
}
