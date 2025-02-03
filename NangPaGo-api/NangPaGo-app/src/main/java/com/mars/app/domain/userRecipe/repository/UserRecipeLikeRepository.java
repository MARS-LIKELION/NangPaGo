package com.mars.app.domain.userRecipe.repository;

import com.mars.common.model.userRecipe.UserRecipeLike;
import com.mars.common.model.userRecipe.UserRecipe;
import com.mars.common.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRecipeLikeRepository extends JpaRepository<UserRecipeLike, Long> {

    long countByUserRecipeId(Long userRecipeId);
    Optional<UserRecipeLike> findByUserAndUserRecipe(User user, UserRecipe userRecipe);
}
