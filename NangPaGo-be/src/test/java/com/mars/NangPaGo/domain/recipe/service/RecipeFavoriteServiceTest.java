package com.mars.NangPaGo.domain.recipe.service;

import com.mars.NangPaGo.domain.recipe.dto.RecipeListResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class RecipeFavoriteServiceTest {
    @Autowired
    private RecipeFavoriteService recipeFavoriteService;

    @DisplayName("사용자의 즐겨찾기된 레시피 조회")
    @Test
    void findSortedFavoritRecipes() {
        // given
        String email = "dummy@nangpago.com";
        int page = 1;

        // when
        Page<RecipeListResponseDto> recipeListResponseDtoPage = recipeFavoriteService.findSortedFavoritRecipes(email, page);

        // then
        assertThat(recipeListResponseDtoPage.hasContent()).isEqualTo(true); //데이터 유무
        assertThat(recipeListResponseDtoPage.getTotalElements()).isGreaterThan(0L);

        System.out.println(recipeListResponseDtoPage.getTotalElements());
        System.out.println("총 페이지 수:" + recipeListResponseDtoPage.getTotalPages());
    }
}