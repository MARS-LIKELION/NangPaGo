package com.mars.NangPaGo.domain.recipe.repository;

import com.mars.NangPaGo.domain.recipe.entity.RecipeFavorite;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@Transactional
@SpringBootTest
class RecipeFavoriteRepositoryTest {
    @Autowired
    private RecipeFavoriteRepository recipeFavoriteRepository;

    @DisplayName("RecipeFavoriteRepository 토글 사용, 생성 또는 삭제")
    @Test
    void useRecipeFavoriteRepository() {
        // given
        String email = "dummy@nangpago.com";
        Long recipeId = 1L;

        // when
        Optional<RecipeFavorite> recipeFavorite = recipeFavoriteRepository.findByEmailAndRecipeId(email,recipeId);

        // then
        assertThat(recipeFavorite).isPresent();
        assertThat(recipeFavorite.get().getUser().getEmail()).isEqualTo(email);
    }
}