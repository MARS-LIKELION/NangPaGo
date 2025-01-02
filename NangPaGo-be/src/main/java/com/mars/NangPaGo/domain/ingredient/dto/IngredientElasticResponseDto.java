package com.mars.NangPaGo.domain.ingredient.dto;

import com.mars.NangPaGo.domain.ingredient.entity.IngredientElastic;
import lombok.Builder;

@Builder
public record IngredientElasticResponseDto(
    String id,
    String name,
    Double matchScore
) {

    public static IngredientElasticResponseDto from(IngredientElastic ingredientElastic, String highlightedName, double matchScore) {
        return IngredientElasticResponseDto.builder()
            .id(ingredientElastic.getId())
            .name(highlightedName)
            .matchScore(matchScore)
            .build();
    }
}

