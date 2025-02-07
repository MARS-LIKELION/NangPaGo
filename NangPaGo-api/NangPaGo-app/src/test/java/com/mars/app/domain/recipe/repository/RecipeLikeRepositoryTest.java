package com.mars.app.domain.recipe.repository;

import com.mars.common.model.recipe.RecipeLike;
import com.mars.app.support.AbstractRepositoryTestSupport;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class RecipeLikeRepositoryTest extends AbstractRepositoryTestSupport {

    @Autowired
    private RecipeLikeRepository recipeLikeRepository;

    @DisplayName("RecipeLike 테이블 findById")
    @Test
    void findById() {
        // given
        Long id = 1L;

        // when
        RecipeLike byId = recipeLikeRepository.findById(id).orElse(null);

        // then
        if (byId != null) {
            System.out.println(byId.getUser().getName());
        }
    }

    @DisplayName("findByUserAndRecipe 쿼리 확인")
    @Test
    void findByUserAndRecipe() {
        // given
        Long userId = 2L;
        Long recipeId = 2L;

        // when
        Optional<RecipeLike> findRecipeLike = recipeLikeRepository.findByUserIdAndRecipeId(userId, recipeId);

        // then
        findRecipeLike.ifPresent(recipeLike ->
            System.out.println(recipeLike.getUser().getName())
        );
    }
    
    @DisplayName("findRecipeLikesByUserId 쿼리 확인")
    @Test
    void findRecipeLikesByUserId() {
        // given
        Long userId = 6L;
        
        // when
        List<RecipeLike> recipeLikesByUserId = recipeLikeRepository.findRecipeLikesByUserId(userId);

        // then
        for (RecipeLike recipeLike : recipeLikesByUserId) {
            System.out.println("recipeLike.getRecipe().getName() = " + recipeLike.getRecipe().getName());
        }
    }
}
