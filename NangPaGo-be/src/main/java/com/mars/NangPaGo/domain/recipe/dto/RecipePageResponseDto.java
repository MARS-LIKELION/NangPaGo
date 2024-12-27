package com.mars.NangPaGo.domain.recipe.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record RecipePageResponseDto(
        Page<RecipeListResponseDto> recipePage,
        boolean prevPage,
        boolean nextPage,
        int startPage,
        int endPage
) {
    public static RecipePageResponseDto createPageInfo(Page<RecipeListResponseDto> recipes, boolean prevPage,
                                                       boolean nextPage, int startPage, int endPage) {
        return RecipePageResponseDto.builder()
                .recipePage(recipes)
                .prevPage(prevPage)
                .nextPage(nextPage)
                .startPage(startPage)
                .endPage(endPage)
                .build();
    }
}
