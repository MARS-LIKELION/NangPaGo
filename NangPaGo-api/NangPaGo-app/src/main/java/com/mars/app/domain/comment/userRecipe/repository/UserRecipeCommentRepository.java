package com.mars.app.domain.comment.userRecipe.repository;

import com.mars.common.model.comment.userRecipe.UserRecipeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRecipeCommentRepository extends JpaRepository<UserRecipeComment, Long> {

    @Query("SELECT urc FROM UserRecipeComment urc JOIN urc.userRecipe ur " +
        "WHERE ur.id = :userRecipeId ORDER BY urc.updatedAt DESC")
    Page<UserRecipeComment> findByUserRecipeId(@Param("userRecipeId") Long userRecipeId, Pageable pageable);


    long countByUserRecipeId(Long userRecipeId);
}
