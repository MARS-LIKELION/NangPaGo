package com.mars.app.domain.user_recipe.service;

import com.mars.app.domain.user_recipe.repository.UserRecipeLikeRepository;
import com.mars.app.domain.user_recipe.repository.UserRecipeRepository;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserRecipeLikeService {

    private final UserRecipeLikeRepository userRecipeLikeRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final UserRepository userRepository;

    public boolean isLiked(Long recipeId, Long userId) {
        return userRecipeLikeRepository.findByUserIdAndUserRecipeId(userId, recipeId).isPresent();
    }

    public long getLikeCount(Long recipeId) {
        return userRecipeLikeRepository.countByUserRecipeId(recipeId);
    }
}
