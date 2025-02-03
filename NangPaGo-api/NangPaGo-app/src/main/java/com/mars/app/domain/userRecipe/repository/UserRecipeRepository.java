package com.mars.app.domain.userRecipe.repository;

import com.mars.common.model.userRecipe.UserRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Long> {

    Page<UserRecipe> findByIsPublicTrueOrUserId(Long userId, Pageable pageable);
}
