package com.mars.NangPaGo.domain.recipe.repository;

import com.mars.NangPaGo.domain.recipe.entity.RecipeLike;
import com.mars.NangPaGo.support.AbstractRepositoryTestSupport;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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
        String email = "dummy@nangpago.com";
        Long recipeId = 2L;

        // when
        Optional<RecipeLike> findRecipeLike = recipeLikeRepository.findByEmailAndRecipeId(email, recipeId);

        // then
        findRecipeLike.ifPresent(recipeLike ->
            System.out.println(recipeLike.getUser().getName())
        );
    }
}
