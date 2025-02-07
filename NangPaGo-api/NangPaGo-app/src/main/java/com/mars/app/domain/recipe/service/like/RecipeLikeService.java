package com.mars.app.domain.recipe.service.like;

import com.mars.app.domain.recipe.repository.like.RecipeLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeLikeService {

    private final RecipeLikeRepository recipeLikeRepository;

    public boolean isLiked(Long recipeId, Long userId) {
        return recipeLikeRepository.findByUserIdAndRecipeId(userId, recipeId).isPresent();
    }

    public int getLikeCount(Long recipeId) {
        return recipeLikeRepository.countByRecipeId(recipeId);
    }
}
