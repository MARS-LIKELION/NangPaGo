package com.mars.NangPaGo.domain.comment.recipe.repository;

import com.mars.NangPaGo.domain.comment.recipe.entity.RecipeComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {
    List<RecipeComment> findByRecipeId(Long recipeId);
}
