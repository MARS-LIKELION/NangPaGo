package com.mars.NangPaGo.domain.refrigerator.dto;

import com.mars.NangPaGo.domain.refrigerator.entity.Refrigerator;
import lombok.Builder;

@Builder
public record RefrigeratorResponseDto(
    String ingredientName
) {
    public static RefrigeratorResponseDto from(String ingredientName) {
        return RefrigeratorResponseDto.builder()
            .ingredientName(ingredientName)
            .build();
    }

    public static RefrigeratorResponseDto from(Refrigerator refrigerator) {
        return RefrigeratorResponseDto.from(refrigerator.getIngredient().getName());
    }
}
