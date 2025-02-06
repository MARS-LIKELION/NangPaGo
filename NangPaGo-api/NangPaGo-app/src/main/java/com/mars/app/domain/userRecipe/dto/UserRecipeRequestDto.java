package com.mars.app.domain.userRecipe.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserRecipeRequestDto {

    @Size(max = 100, message = "게시물 제목은 최대 100자까지 입력할 수 있습니다.")
    private String title;
    private String content;
    private boolean isPublic;
    private String mainImageUrl;
    private List<IngredientDto> ingredients;
    private List<ManualDto> manuals;

    @Getter
    @Builder
    public static class IngredientDto {
        private String name;
        private String amount;

        public static IngredientDto of(String name, String amount) {
            return IngredientDto.builder()
                .name(name)
                .amount(amount)
                .build();
        }
    }

    @Getter
    @Builder
    public static class ManualDto {
        private int step;
        private String description;
        private String imageUrl;

        public static ManualDto of(int step, String description, String imageUrl) {
            return ManualDto.builder()
                .step(step)
                .description(description)
                .imageUrl(imageUrl)
                .build();
        }
    }
}
