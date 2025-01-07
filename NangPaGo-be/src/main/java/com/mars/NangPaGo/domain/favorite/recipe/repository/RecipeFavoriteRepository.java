package com.mars.NangPaGo.domain.favorite.recipe.repository;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

import com.mars.NangPaGo.domain.favorite.recipe.entity.RecipeFavorite;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeFavoriteRepository extends JpaRepository<RecipeFavorite, Long> {

    @Lock(PESSIMISTIC_WRITE)
    Optional<RecipeFavorite> findByUserAndRecipe(User user, Recipe recipe);

    @Query("SELECT rf FROM RecipeFavorite rf WHERE rf.user = :user")
    Page<RecipeFavorite> findAllByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT rf FROM RecipeFavorite rf WHERE rf.user.email = :email AND rf.recipe.id = :recipeId")
    Optional<RecipeFavorite> findByEmailAndRecipeId(@Param("email") String email, @Param("recipeId") Long recipeId);

    int countByUser(User user);
}
