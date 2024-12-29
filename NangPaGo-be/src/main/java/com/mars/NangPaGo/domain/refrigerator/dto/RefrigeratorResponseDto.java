package com.mars.NangPaGo.domain.refrigerator.dto;

import com.mars.NangPaGo.domain.refrigerator.entity.Refrigerator;
import lombok.Builder;

@Builder
public record RefrigeratorResponseDto(
        // 수량같은 데이터 확장을 생각하면 String으로 받아서 반환하는거보다
        // Dto를 만들어 두는게 나을거같아서 Dto사용
        String ingredientName
) {
    public static RefrigeratorResponseDto toDto(Refrigerator refrigerator) {
        return RefrigeratorResponseDto.builder()
                .ingredientName(refrigerator.getIngredient().getName())
                .build();
    }
}