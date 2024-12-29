package com.mars.NangPaGo.domain.recipe.repository;

import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.entity.RecipeLike;
import com.mars.NangPaGo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RecipeLike> findWithLockByUserAndRecipe(User user, Recipe recipe);

    @Query("SELECT rl FROM RecipeLike rl WHERE rl.user.email = :email AND rl.recipe.id = :recipeId")
    Optional<RecipeLike> findByEmailAndRecipeId(@Param("email") String email, @Param("recipeId") Long recipeId);
}
