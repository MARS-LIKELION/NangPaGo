package com.mars.NangPaGo.domain.comment.recipe.repository;

import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.NangPaGo.domain.comment.recipe.entity.RecipeComment;
import com.mars.NangPaGo.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {

    Page<RecipeComment> findByRecipeId(Long recipeId, Pageable pageable);

    Page<RecipeComment> findByUserEmail(String email, Pageable pageable);

    int countByRecipeId(Long recipeId);

    int countByUser(User user);
}
