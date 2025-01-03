package com.mars.NangPaGo.domain.ingredient.dto;

import com.mars.NangPaGo.domain.ingredient.entity.IngredientEs;
import lombok.Builder;

@Builder
public record IngredientEsResponseDto(
    String id,
    String name,
    Double matchScore
) {

    public static IngredientEsResponseDto of(IngredientEs ingredientEs, String highlightedName, double matchScore) {
        return IngredientEsResponseDto.builder()
            .id(ingredientEs.getId())
            .name(highlightedName)
            .matchScore(matchScore)
            .build();
    }
}

