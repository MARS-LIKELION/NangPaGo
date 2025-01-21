package com.mars.app.domain.comment.recipe.repository;

import com.mars.common.model.comment.recipe.RecipeComment;
import com.mars.common.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {

    Page<RecipeComment> findByRecipeId(Long recipeId, Pageable pageable);

    @Query("SELECT rc FROM RecipeComment rc JOIN FETCH rc.recipe WHERE rc.user.email = :email ORDER BY rc.updatedAt DESC")
    Page<RecipeComment> findByUserEmailWithRecipe(@Param("email") String email, Pageable pageable);

    int countByRecipeId(Long recipeId);

    int countByUser(User user);
}
