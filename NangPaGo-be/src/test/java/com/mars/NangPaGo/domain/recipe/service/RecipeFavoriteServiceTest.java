package com.mars.NangPaGo.domain.recipe.service;

import com.mars.NangPaGo.domain.recipe.dto.RecipePageResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class RecipeFavoriteServiceTest {
    @Autowired
    private RecipeFavoriteService recipeFavoriteService;

    @DisplayName("사용자의 즐겨찾기된 레시피 조회")
    @Test
    void findSortedFavoriteRecipes() {
        // given
        String email = "dummy@nangpago.com";
        int page = 1;

        // when
        RecipePageResponseDto recipes = recipeFavoriteService.findSortedFavoriteRecipes(email, page);

        // then
        assertThat(recipes.recipePage().hasContent()).isEqualTo(true); //데이터 유무
        assertThat(recipes.recipePage().getTotalElements()).isGreaterThan(0L);
    }
}