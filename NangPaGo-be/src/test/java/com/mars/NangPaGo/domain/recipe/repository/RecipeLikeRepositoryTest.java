package com.mars.NangPaGo.domain.recipe.repository;

import com.mars.NangPaGo.domain.recipe.entity.RecipeLike;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class RecipeLikeRepositoryTest {

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
