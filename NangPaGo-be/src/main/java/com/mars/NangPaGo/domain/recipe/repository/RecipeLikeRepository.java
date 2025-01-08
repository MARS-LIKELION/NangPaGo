package com.mars.NangPaGo.domain.recipe.repository;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.entity.RecipeLike;
import com.mars.NangPaGo.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {

    @Lock(PESSIMISTIC_WRITE)
    Optional<RecipeLike> findByUserAndRecipe(User user, Recipe recipe);

    @Query("SELECT rl FROM RecipeLike rl WHERE rl.user.email = :email AND rl.recipe.id = :recipeId")
    Optional<RecipeLike> findByEmailAndRecipeId(@Param("email") String email, @Param("recipeId") Long recipeId);

    Page<RecipeLike> findRecipeLikeByUser(User user, Pageable pageable);

    @Query("SELECT COUNT(rl) FROM RecipeLike rl WHERE rl.recipe.id = :recipeId")
    int countByRecipeId(@Param("recipeId") Long recipeId);

    int countByUser(User user);
}
