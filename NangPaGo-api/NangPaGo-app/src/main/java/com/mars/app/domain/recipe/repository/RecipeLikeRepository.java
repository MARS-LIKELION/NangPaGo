package com.mars.app.domain.recipe.repository;

import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.recipe.RecipeLike;
import com.mars.common.model.user.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    Optional<RecipeLike> findByUserAndRecipe(User user, Recipe recipe);

    @Query("SELECT rl FROM RecipeLike rl WHERE rl.user.email = :email AND rl.recipe.id = :recipeId")
    Optional<RecipeLike> findByEmailAndRecipeId(@Param("email") String email, @Param("recipeId") Long recipeId);

    Page<RecipeLike> findRecipeLikeByUser(User user, Pageable pageable);

    @Query("SELECT COUNT(rl) FROM RecipeLike rl WHERE rl.recipe.id = :recipeId")
    int countByRecipeId(@Param("recipeId") Long recipeId);

    int countByUser(User user);
}
