package com.mars.app.domain.recipe.service;

import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeLikeService {

    private final RecipeLikeRepository recipeLikeRepository;

    public boolean isLiked(Long recipeId, String email) {
        return recipeLikeRepository.findByEmailAndRecipeId(email, recipeId).isPresent();
    }

    public int getLikeCount(Long recipeId) {
        return recipeLikeRepository.countByRecipeId(recipeId);
    }
}
