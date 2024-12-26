package com.mars.NangPaGo.domain.recipe.repository;

import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.entity.RecipeFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeFavoriteRepository extends JpaRepository<RecipeFavorite, Long> {

    @Query("SELECT rf FROM RecipeFavorite rf WHERE rf.user.email = :email AND rf.recipe.id = :recipeId")
    Optional<RecipeFavorite> findByEmailAndRecipeId(@Param("email") String email, @Param("recipeId") Long recipeId);

    @Query("SELECT rf.recipe FROM RecipeFavorite rf " +
            "WHERE rf.user.email = :email " +
            "ORDER BY rf.createdAt DESC")
    Page<Recipe> findByEmail(@Param("email") String email, Pageable pageable);
}